package com.ivar.audit.modules.security.dto;

import com.ivar.audit.modules.security.enums.EventSeverity;
import com.ivar.audit.modules.security.enums.SecurityEventType;

public record SecurityEventRequest(
	    SecurityEventType eventType,
	    EventSeverity severity,
	    String source,
	    String details
	) {}