package com.ivar.audit.modules.audit.domains.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;

import java.time.Instant;
@Entity
@Table(name = "audit_logs")
public class AuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private String tenantId;
	private String actorId;
	private String actorRole;
	private String action;
	private Instant timestamp;
	private String hash;
	private String previousHash;

	public AuditLog(Long id, String tenantId, String actorId, String actorRole, String action, Instant timestamp,
			String hash, String previousHash) {
		super();
		this.id = id;
		this.tenantId = tenantId;
		this.actorId = actorId;
		this.actorRole = actorRole;
		this.action = action;
		this.timestamp = timestamp;
		this.hash = hash;
		this.previousHash = previousHash;
	}


	@Override
	public String toString() {
		return "AuditLog [id=" + id + ", tenantId=" + tenantId + ", actorId=" + actorId + ", actorRole=" + actorRole
				+ ", action=" + action + ", timestamp=" + timestamp + ", hash=" + hash + ", previousHahs="
				+ previousHash + "]";
	}




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




	public String getActorId() {
		return actorId;
	}




	public void setActorId(String actorId) {
		this.actorId = actorId;
	}




	public String getActorRole() {
		return actorRole;
	}




	public void setActorRole(String actorRole) {
		this.actorRole = actorRole;
	}




	public String getAction() {
		return action;
	}




	public void setAction(String action) {
		this.action = action;
	}




	public Instant getTimestamp() {
		return timestamp;
	}




	public void setTimestamp(Instant timestamp) {
		this.timestamp = timestamp;
	}




	public String getHash() {
		return hash;
	}




	public void setHash(String hash) {
		this.hash = hash;
	}




	public String getPreviousHash() {
		return previousHash;
	}




	public void setPreviousHash(String previousHash) {
		this.previousHash = previousHash;
	}




	public AuditLog() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
}
