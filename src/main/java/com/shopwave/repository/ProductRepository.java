//ATE/8400/14
package com.shopwave.repository;

import com.shopwave.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    // Fetch all products belonging to a specific category
    List<Product> findByCategoryId(Long categoryId);

    // Fetch all products at or below a given price ceiling
    List<Product> findByPriceLessThanEqual(BigDecimal maxPrice);

    // Case-insensitive keyword search against product name
    List<Product> findByNameContainingIgnoreCase(String keyword);

    // Most expensive product across the entire catalog
    Optional<Product> findTopByOrderByPriceDesc();

    // Combined search — filter by keyword AND price ceiling in one query
    @Query("SELECT p FROM Product p WHERE " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) AND " +
            "p.price <= :maxPrice")
    List<Product> findByNameContainingIgnoreCaseAndPriceLessThanEqual(
            String keyword,
            BigDecimal maxPrice
    );
}