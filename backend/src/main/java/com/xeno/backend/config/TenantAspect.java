package com.xeno.backend.config;

import jakarta.persistence.EntityManager;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TenantAspect {
    @Autowired private EntityManager entityManager;

    @Before("execution(* com.xeno.backend.service..*(..))")
    public void enableTenantFilter() {
        String tenant = TenantContext.getCurrentTenant();
        if (tenant != null) {
            Session session = entityManager.unwrap(Session.class);
            session.enableFilter("tenantFilter").setParameter("shopDomain", tenant);
        }
    }
}