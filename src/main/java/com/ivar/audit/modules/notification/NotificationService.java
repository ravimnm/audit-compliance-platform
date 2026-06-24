package com.ivar.audit.modules.notification;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
@Service
public class NotificationService {
	private final JavaMailSender mailSender;
	public NotificationService(JavaMailSender mailSender){
		this.mailSender=mailSender;
	}
	public void sendAlert(String subject,String message) {
		SimpleMailMessage mail=new SimpleMailMessage();
		
		mail.setTo("2005ravishan@gmail.com");
		mail.setSubject(subject);
		mail.setText(message);
		mailSender.send(mail);
		
	}

}
