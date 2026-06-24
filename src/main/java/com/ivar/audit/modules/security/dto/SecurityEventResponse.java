package com.ivar.audit.modules.security.dto;

import java.time.Instant;

import com.ivar.audit.modules.security.enums.EventSeverity;
import com.ivar.audit.modules.security.enums.SecurityEventType;

public record SecurityEventResponse(
	    Long id,
	    SecurityEventType eventType,
	    EventSeverity severity,
	    String source,
	    String details,
	    Instant timestamp
	) {}