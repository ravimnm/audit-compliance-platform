package com.ivar.audit.modules.security.dto;

public record SecurityVerificationResponse(
        boolean valid,
        String message
) {}