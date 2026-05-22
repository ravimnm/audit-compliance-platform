package com.ivar.audit.modules.audit.service;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.ivar.audit.common.context.TenantContext;
import com.ivar.audit.common.response.PagedResponse;
import com.ivar.audit.common.utils.HashUtil;
import com.ivar.audit.modules.audit.domains.entity.AuditLog;
import com.ivar.audit.modules.audit.dto.AuditLogRequest;
import com.ivar.audit.modules.audit.dto.AuditLogResponse;
import com.ivar.audit.modules.audit.dto.AuditVerificationResponse;
import com.ivar.audit.modules.audit.repository.AuditLogRepository;
import com.ivar.audit.modules.audit.repository.specification.AuditLogSpecification;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public AuditLogResponse createAuditLog(AuditLogRequest req) {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("Unauthenticated request");
        }

        String userId = (String) authentication.getPrincipal();


        String role = authentication.getAuthorities()
                .stream()
                .findFirst()
                .map(a -> a.getAuthority())
                .orElse("ROLE_USER");

        String tenantId = TenantContext.getTenant();

        if (tenantId == null) {
            throw new RuntimeException("Tenant not found in context");
        }
        AuditLog lastLog = auditLogRepository
                .findTopByTenantIdOrderByTimestampDesc(tenantId);

        String previousHash = lastLog != null
                ? lastLog.getHash()
                : "GENESIS";

        // Create deterministic data string
        Instant now= Instant.now();
        String data = userId + role + tenantId + req.action() + now.toString();

        String hash = HashUtil.sha256(previousHash + data);

        AuditLog auditLog = new AuditLog();
        auditLog.setAction(req.action());
        auditLog.setActorId(userId);
        auditLog.setActorRole(role);
        auditLog.setTenantId(tenantId);
        auditLog.setTimestamp(now);
        auditLog.setPreviousHash(previousHash);
        auditLog.setHash(hash);
        
        AuditLog savedLog = auditLogRepository.save(auditLog);


        try {
			Files.writeString(
				    Path.of("integrity_anchor.txt"),
				    savedLog.getHash(),
				    StandardOpenOption.CREATE,
				    StandardOpenOption.TRUNCATE_EXISTING
				);
		} catch (IOException e) {
	        throw new RuntimeException("Failed to write integrity anchor", e);
	    }
        return new AuditLogResponse(
                savedLog.getId(),
                savedLog.getActorId(),
                savedLog.getActorRole(),
                savedLog.getTenantId(),
                savedLog.getAction(),
                savedLog.getTimestamp()
        );
    }
    
    public PagedResponse<AuditLogResponse> getLogs(
            String actorId,
            String action,
            Instant from,
            Instant to,
            int page,
            int size,
            String sortBy,
            String direction
    ) {

        String tenantId = TenantContext.getTenant();

        if (tenantId == null) {
            throw new RuntimeException("Tenant not found in context");
        }

        Specification<AuditLog> spec = Specification
                .where(AuditLogSpecification.byTenant(tenantId))
                .and(AuditLogSpecification.byActor(actorId))
                .and(AuditLogSpecification.byAction(action))
                .and(AuditLogSpecification.byDateRange(from, to));

        
        Authentication auth = SecurityContextHolder
        		.getContext()
        		.getAuthentication();
        
        String userId= (String) auth.getPrincipal();
        
        String role = auth.getAuthorities()
        		.stream()
        		.findFirst()
        		.map(a->a.getAuthority())
        		.orElse("ROLE_USER");
        
        if(role.equals("ROLE_USER")) {
        	spec = spec.and(AuditLogSpecification.byActor(userId));
        }
        
        Sort sort = direction.equalsIgnoreCase("asc") ?
                Sort.by(sortBy).ascending() :
                Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);

        Page<AuditLog> result = auditLogRepository.findAll(spec, pageable);

        List<AuditLogResponse> data= result.getContent()
        		.stream()
        		.map(log -> new AuditLogResponse(
        				log.getId(),
        				log.getActorId(),
        				log.getActorRole(),
        				log.getTenantId(),
        				log.getAction(),
        				log.getTimestamp()
        		))
        		.toList();
        
        return new PagedResponse<>(
        		data,
        		result.getNumber(),
        		result.getSize(),
        		result.getTotalElements(),
        		result.getTotalPages(),
        		result.hasNext()
        );
    }
    
    public AuditVerificationResponse verifyChain() {
    	String tenantId=TenantContext.getTenant();
    	if(tenantId==null) {
    		throw new RuntimeException("Tenant not found in context");
    	}
    	
    	List<AuditLog> logs=auditLogRepository.findByTenantIdOrderByTimestampAsc(tenantId);
    	if (logs.isEmpty()) {
            return new AuditVerificationResponse(true, "No logs to verify");
        }
    	String previousHash= "GENESIS";
    	
    	for(AuditLog log: logs) {
    		String data = log.getActorId()
                    + log.getActorRole()
                    + log.getTenantId()
                    + log.getAction()
                    + log.getTimestamp().toString();
    		
    		String recalculatedHash = HashUtil.sha256(previousHash+data);
    		if (!recalculatedHash.equals(log.getHash())) {
                return new AuditVerificationResponse(
                        false,
                        "Tampering detected at log ID: " + log.getId()
                );
            }
    		previousHash = log.getHash();
    	}
    	try {
            String anchorHash = Files.readString(Path.of("integrity_anchor.txt"));

            AuditLog lastLog = logs.get(logs.size() - 1);

            if (!lastLog.getHash().equals(anchorHash)) {
                return new AuditVerificationResponse(
                        false,
                        "Anchor mismatch: possible chain rewrite attack"
                );
            }

        } catch (IOException e) {
            return new AuditVerificationResponse(
                    false,
                    "Failed to read integrity anchor"
            );
        }
    	
    	return new AuditVerificationResponse(true,"Audit log is Valid");
    }
    
    public String exportLogsToCSV() {

        String tenantId = TenantContext.getTenant();

        List<AuditLog> logs = auditLogRepository
                .findByTenantIdOrderByTimestampAsc(tenantId);

        StringBuilder csv = new StringBuilder();

        // Header
        csv.append("id,actorId,role,tenantId,action,timestamp\n");

        for (AuditLog log : logs) {
            csv.append(log.getId()).append(",")
               .append(log.getActorId()).append(",")
               .append(log.getActorRole()).append(",")
               .append(log.getTenantId()).append(",")
               .append(log.getAction()).append(",")
               .append(log.getTimestamp()).append("\n");
        }

        return csv.toString();
    }
}
