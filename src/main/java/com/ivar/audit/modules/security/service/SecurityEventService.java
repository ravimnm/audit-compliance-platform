package com.ivar.audit.modules.security.service;

import java.time.Instant;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.ivar.audit.common.context.TenantContext;
import com.ivar.audit.common.utils.HashUtil;
import com.ivar.audit.modules.audit.domains.entity.AuditLog;
import com.ivar.audit.modules.notification.NotificationService;
import com.ivar.audit.modules.security.dto.SecurityEventRequest;
import com.ivar.audit.modules.security.dto.SecurityEventResponse;
import com.ivar.audit.modules.security.dto.SecurityVerificationResponse;
import com.ivar.audit.modules.security.entity.SecurityEvent;
import com.ivar.audit.modules.security.enums.EventSeverity;
import com.ivar.audit.modules.security.repository.SecurityEventRepository;

@Service
public class SecurityEventService {

    private final SecurityEventRepository repository;
    private final NotificationService notificationService;

    public SecurityEventService(SecurityEventRepository repository,
                                NotificationService notificationService) {

        this.repository = repository;
        this.notificationService = notificationService;
    }

    public SecurityEventResponse createSecurityEvent(
            SecurityEventRequest request) {

        String tenantId = TenantContext.getTenant();

        if (tenantId == null) {
            throw new RuntimeException("Tenant not found in context");
        }

        SecurityEvent lastEvent =
                repository.findTopByTenantIdOrderByTimestampDesc(tenantId);

        String previousHash = lastEvent != null
                ? lastEvent.getHash()
                : "GENESIS";

        Instant now = Instant.now();

        String data =
                request.eventType().name()
                + request.severity().name()
                + request.source()
                + request.details()
                + tenantId
                + now.toString();

        String hash = HashUtil.sha256(previousHash + data);

        SecurityEvent event = new SecurityEvent();

        event.setTenantId(tenantId);
        event.setSource(request.source());
        event.setEventType(request.eventType());
        event.setSeverity(request.severity());
        event.setDetails(request.details());
        event.setTimestamp(now);
        event.setPreviousHash(previousHash);
        event.setHash(hash);

        SecurityEvent saved = repository.save(event);

        if (saved.getSeverity() == EventSeverity.HIGH
                || saved.getSeverity() == EventSeverity.CRITICAL) {

            notificationService.sendAlert(
                    "High Severity Security Event Detected",

                    "Tenant: " + saved.getTenantId()
                    + "\nEvent: " + saved.getEventType()
                    + "\nSeverity: " + saved.getSeverity()
                    + "\nSource: " + saved.getSource()
                    + "\nDetails: " + saved.getDetails()
            );
        }

        return new SecurityEventResponse(
                saved.getId(),
                saved.getEventType(),
                saved.getSeverity(),
                saved.getSource(),
                saved.getDetails(),
                saved.getTimestamp()
        );
    }
    
    public SecurityVerificationResponse verifyChain() {

        String tenantId = TenantContext.getTenant();

        if (tenantId == null) {
            throw new RuntimeException("Tenant not found");
        }

        List<SecurityEvent> events =
                repository.findByTenantIdOrderByTimestampAsc(tenantId);

        if (events.isEmpty()) {
            return new SecurityVerificationResponse(
                    true,
                    "No security events to verify"
            );
        }

        String previousHash = "GENESIS";

        for (SecurityEvent event : events) {

            String data =
                    event.getEventType().name()
                    + event.getSeverity().name()
                    + event.getSource()
                    + event.getDetails()
                    + event.getTenantId()
                    + event.getTimestamp().toString();

            String recalculatedHash =
                    HashUtil.sha256(previousHash + data);

            if (!recalculatedHash.equals(event.getHash())) {

                notificationService.sendAlert(
                        "Security Event Tampering Detected",
                        "Tampering detected at Security Event ID: "
                        + event.getId()
                );

                return new SecurityVerificationResponse(
                        false,
                        "Tampering detected at Security Event ID: "
                        + event.getId()
                );
            }

            previousHash = event.getHash();
        }

        return new SecurityVerificationResponse(
                true,
                "Security Event chain is valid"
        );
    }
    
    public Page<SecurityEventResponse> getEvents(
            int page,
            int size,String sortBy, String direction) {

        String tenantId = TenantContext.getTenant();
        Sort sort = direction.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size,sort);

        Page<SecurityEvent> events =
                repository.findByTenantId(
                        tenantId,
                        pageable);

        return events.map(event ->
                new SecurityEventResponse(
                        event.getId(),
                        event.getEventType(),
                        event.getSeverity(),
                        event.getSource(),
                        event.getDetails(),
                        event.getTimestamp()
                ));
    }
    
    
}