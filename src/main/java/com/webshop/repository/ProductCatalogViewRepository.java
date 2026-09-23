package com.webshop.repository;

import com.webshop.model.ProductCatalogView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductCatalogViewRepository extends JpaRepository<ProductCatalogView, Long> {

    @Query("SELECT v FROM ProductCatalogView v WHERE " +
           "(:categoryId IS NULL OR v.categoryId = :categoryId) AND " +
           "(:supplierId IS NULL OR v.supplierId = :supplierId) AND " +
           "(:search IS NULL OR LOWER(v.productName) LIKE LOWER(CONCAT('%', :search, '%')) " +
           "OR LOWER(v.productDescription) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<ProductCatalogView> searchCatalog(
            @Param("categoryId") Long categoryId,
            @Param("supplierId") Long supplierId,
            @Param("search") String search
    );
}
