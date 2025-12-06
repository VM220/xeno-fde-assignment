package com.xeno.backend.repository;

import com.xeno.backend.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    // Because of the TenantAspect, these queries ONLY see the current tenant's data

    @Query("SELECT SUM(o.totalPrice) FROM Order o")
    BigDecimal sumTotalRevenue();

    long count();
}