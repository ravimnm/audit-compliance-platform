package com.ivar.audit.modules.audit.repository.specification;

import java.time.Instant;

import org.springframework.data.jpa.domain.Specification;

import com.ivar.audit.modules.audit.domains.entity.AuditLog;

public class AuditLogSpecification {

    public static Specification<AuditLog> byTenant(String tenantId) {
        return (root, query, cb) ->
                cb.equal(root.get("tenantId"), tenantId);
    }

    public static Specification<AuditLog> byActor(String actorId) {
        return (root, query, cb) ->
                actorId == null ? null :
                        cb.equal(root.get("actorId"), actorId);
    }

    public static Specification<AuditLog> byAction(String action) {
        return (root, query, cb) ->
                action == null ? null :
                        cb.equal(root.get("action"), action);
    }

    public static Specification<AuditLog> byDateRange(Instant from, Instant to) {
        return (root, query, cb) -> {

            if (from == null && to == null) return null;

            if (from != null && to != null) {
                return cb.between(root.get("timestamp"), from, to);
            }

            if (from != null) {
                return cb.greaterThanOrEqualTo(root.get("timestamp"), from);
            }

            return cb.lessThanOrEqualTo(root.get("timestamp"), to);
        };
    }
}