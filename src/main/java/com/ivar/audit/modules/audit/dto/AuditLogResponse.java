package com.ivar.audit.modules.audit.dto;

import java.time.Instant;

public record AuditLogResponse(Long id,String action,String actorId, String actorRole,String Action, Instant timestamp) {
}