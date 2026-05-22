package com.ivar.audit.modules.audit.dto;

import jakarta.validation.constraints.NotBlank;

public record AuditLogRequest(@NotBlank(message="Action is required") String action,String actorId,String tenantId) {
	
}