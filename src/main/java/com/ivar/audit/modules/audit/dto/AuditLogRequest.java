package com.ivar.audit.modules.audit.dto;

import jakarta.validation.constraints.NotBlank;

public record AuditLogRequest(@NotBlank(message="Action is required") String action,@NotBlank(message="ActionId is required")String actorId,@NotBlank(message="TenantId is required") String tenantId) {
	
}