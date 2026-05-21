package com.ivar.audit.modules.audit.dto;

public class AuditVerificationResponse {
	boolean valid;
	String message;
	public boolean isValid() {
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	@Override
	public String toString() {
		return "AuditVerificationResponse [valid=" + valid + ", message=" + message + "]";
	}
	public AuditVerificationResponse(boolean valid, String message) {
		super();
		this.valid = valid;
		this.message = message;
	}
	public AuditVerificationResponse() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
	
}
