package com.ivar.audit.security.jwt;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.List;
import java.io.IOException;
import java.util.Collections;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ivar.audit.common.context.TenantContext;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter{
	private final JwtTokenProvider jwtTokenProvider;
	
	public JwtFilter(JwtTokenProvider jwtTokenProvider) {
		this.jwtTokenProvider = jwtTokenProvider;
	}
	
	@Override
	protected void doFilterInternal(HttpServletRequest req,
	                               HttpServletResponse res,
	                               FilterChain filterChain)
	        throws ServletException, IOException {

	    try {
	        String authHeader = req.getHeader("Authorization");

	        if (authHeader != null && authHeader.startsWith("Bearer ")) {
	            String token = authHeader.substring(7);

	            String userId = jwtTokenProvider.getUserIdFromToken(token);
	            String role = jwtTokenProvider.getRoleFromToken(token);
	            String tenantId = jwtTokenProvider.gettenantIdFromToken(token);
	            TenantContext.setTenant(tenantId);

	            if (jwtTokenProvider.validateToken(token, userId)) {

	                var auth = new UsernamePasswordAuthenticationToken(
	                        userId,
	                        null,
	                        List.of(new SimpleGrantedAuthority("ROLE_" + role))
	                );

	                SecurityContextHolder.getContext().setAuthentication(auth);

	                // set per-request tenant
	                TenantContext.setTenant(tenantId);
	            }
	        }

	        filterChain.doFilter(req, res);

	    } finally {
	        // ALWAYS clear (even if an exception happens downstream)
	        TenantContext.clear();
	        SecurityContextHolder.clearContext(); // optional but good hygiene
	    }
	}

}
