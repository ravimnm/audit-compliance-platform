package com.ivar.audit.modules.audit.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.ivar.audit.modules.audit.domains.entity.AuditLog;
import com.ivar.audit.modules.audit.dto.AuditLogResponse;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long>,JpaSpecificationExecutor<AuditLog> {
	List<AuditLog> findByTenantId(String tenantId);
	AuditLog findTopByTenantIdOrderByTimestampDesc(String tenantId);
	List<AuditLog> findByTenantIdOrderByTimestampAsc(String tenantId);
}
