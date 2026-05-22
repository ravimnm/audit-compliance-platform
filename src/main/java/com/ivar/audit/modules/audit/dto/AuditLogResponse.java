package com.ivar.audit.modules.audit.dto;

import java.time.Instant;
public record AuditLogResponse(
	    Long id,
	    String actorId,
	    String actorRole,
	    String tenantId,
	    String action,
	    Instant timestamp
	) {}