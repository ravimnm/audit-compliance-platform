package com.ivar.audit.modules.audit.dto;

import java.time.Instant;

public record AuditLogQueryRequest(
        String actorId,
        String action,
        Instant from,
        Instant to,
        Integer page,
        Integer size,
        String sortBy,
        String direction
) {
	
	
}