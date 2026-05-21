package com.ivar.audit.modules.audit.controller;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ivar.audit.common.response.ApiResponse;
import com.ivar.audit.common.response.PagedResponse;
import com.ivar.audit.modules.audit.dto.AuditLogQueryRequest;
import com.ivar.audit.modules.audit.dto.AuditLogRequest;
import com.ivar.audit.modules.audit.dto.AuditLogResponse;
import com.ivar.audit.modules.audit.dto.AuditVerificationResponse;
import com.ivar.audit.modules.audit.service.AuditLogService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/audit/events")
public class AuditLogController {
	private final AuditLogService auditLogService;
	public AuditLogController(AuditLogService auditLogService) {
		this.auditLogService=auditLogService;
	}
	
	@PostMapping
	public ApiResponse<AuditLogResponse> createAuditLog(@Valid @RequestBody AuditLogRequest req) {
		AuditLogResponse response = auditLogService.createAuditLog(req);
		return new ApiResponse<>(response);
	}

    @GetMapping
    public ApiResponse<PagedResponse<AuditLogResponse>> getLogs(@ModelAttribute AuditLogQueryRequest req) {

        PagedResponse<AuditLogResponse> response = auditLogService.getLogs(
                req.actorId(),
                req.action(),
                req.from(),
                req.to(),
                req.page() !=null? req.page():0,
                req.size() !=null? req.size():10,
                req.sortBy()!=null? req.sortBy():"timestamp",
                req.direction()!=null? req.direction():"desc"
        );

        return new ApiResponse<>(response);
    }
    
    @GetMapping("/verify")
    public ApiResponse<AuditVerificationResponse> verifyLogs() {
        return new ApiResponse<>(auditLogService.verifyChain());
    }
    
    @GetMapping("/export")
    public ResponseEntity<String> exportLogs() {

        String csv = auditLogService.exportLogsToCSV();

        return ResponseEntity.ok()
                .header("Content-Disposition", "attachment; filename=audit_logs.csv")
                .body(csv);
    }

}
