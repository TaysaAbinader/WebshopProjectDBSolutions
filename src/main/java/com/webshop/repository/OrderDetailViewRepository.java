package com.webshop.repository;

import com.webshop.model.OrderDetailView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderDetailViewRepository extends JpaRepository<OrderDetailView, Long> {

    @Query("SELECT v FROM OrderDetailView v WHERE " +
           "(:customerId IS NULL OR v.customerId = :customerId) AND " +
           "(:status IS NULL OR v.orderStatus = :status)")
    List<OrderDetailView> searchOrders(
            @Param("customerId") Long customerId,
            @Param("status") String status
    );
}
