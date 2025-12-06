package com.xeno.backend.entity;

import com.xeno.backend.config.TenantContext;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;

@MappedSuperclass
@Data
@FilterDef(name = "tenantFilter", parameters = @ParamDef(name = "shopDomain", type = String.class))
@Filter(name = "tenantFilter", condition = "shop_domain = :shopDomain")
public abstract class BaseEntity {
    @Column(name = "shop_domain")
    private String shopDomain;

    @PrePersist
    public void onPrePersist() {
        if (shopDomain == null) {
            shopDomain = TenantContext.getCurrentTenant();
        }
    }
}