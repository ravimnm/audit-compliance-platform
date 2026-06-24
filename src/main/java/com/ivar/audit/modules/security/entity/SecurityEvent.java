package com.ivar.audit.modules.security.entity;

import java.time.Instant;

import com.ivar.audit.modules.security.enums.EventSeverity;
import com.ivar.audit.modules.security.enums.SecurityEventType;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name="security_events")
public class SecurityEvent {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	Long id;

	String tenantId;

	String source;

	SecurityEventType eventType;

	EventSeverity severity;

	String details;

	Instant timestamp;

	String previousHash;

	String hash;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTenantId() {
		return tenantId;
	}

	public void setTenantId(String tenantId) {
		this.tenantId = tenantId;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public SecurityEventType getEventType() {
		return eventType;
	}

	public void setEventType(SecurityEventType eventType) {
		this.eventType = eventType;
	}

	public EventSeverity getSeverity() {
		return severity;
	}

	public void setSeverity(EventSeverity severity) {
		this.severity = severity;
	}

	public String getDetails() {
		return details;
	}

	public void setDetails(String details) {
		this.details = details;
	}

	public Instant getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Instant timestamp) {
		this.timestamp = timestamp;
	}

	public String getPreviousHash() {
		return previousHash;
	}

	public void setPreviousHash(String previousHash) {
		this.previousHash = previousHash;
	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	@Override
	public String toString() {
		return "SecurityEvent [id=" + id + ", tenantId=" + tenantId + ", source=" + source + ", eventType=" + eventType
				+ ", severity=" + severity + ", details=" + details + ", timestamp=" + timestamp + ", previousHash="
				+ previousHash + ", hash=" + hash + "]";
	}

	public SecurityEvent(Long id, String tenantId, String source, SecurityEventType eventType, EventSeverity severity,
			String details, Instant timestamp, String previousHash, String hash) {
		super();
		this.id = id;
		this.tenantId = tenantId;
		this.source = source;
		this.eventType = eventType;
		this.severity = severity;
		this.details = details;
		this.timestamp = timestamp;
		this.previousHash = previousHash;
		this.hash = hash;
	}

	public SecurityEvent() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	

}
