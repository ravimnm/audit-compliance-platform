package com.ivar.audit.modules.notification;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController {
	private final NotificationService notificationService;
	public NotificationController(NotificationService notificationService){
		this.notificationService = notificationService;
	}
	
	@GetMapping("/test-email")
	public String sendEmail() {
		notificationService.sendAlert("SMTAP Mail", "SMTAP is working properly");
		return "Email Sent";
	}
}
