package com.shopwave.repository;

import com.shopwave.model.Category;
import com.shopwave.model.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;
import com.shopwave.shopwave_starter.ShopwaveStarterApplication;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ContextConfiguration(classes = ShopwaveStarterApplication.class)
class ProductRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    private Category category;

    @BeforeEach
    void setUp() {
        // persist a category first since product requires it
        category = entityManager.persistAndFlush(
                Category.builder()
                        .name("Electronics")
                        .description("Electronic devices")
                        .build()
        );

        // persist three products with distinct names and prices
        entityManager.persistAndFlush(
                Product.builder()
                        .name("Wireless Keyboard")
                        .description("Compact keyboard")
                        .price(new BigDecimal("49.99"))
                        .stock(20)
                        .category(category)
                        .build()
        );

        entityManager.persistAndFlush(
                Product.builder()
                        .name("Wireless Mouse")
                        .description("Ergonomic mouse")
                        .price(new BigDecimal("29.99"))
                        .stock(35)
                        .category(category)
                        .build()
        );

        entityManager.persistAndFlush(
                Product.builder()
                        .name("USB Hub")
                        .description("7-port hub")
                        .price(new BigDecimal("19.99"))
                        .stock(50)
                        .category(category)
                        .build()
        );
    }

    @Test
    void findByNameContainingIgnoreCase_withMatchingKeyword_returnsCorrectResults() {
        // "wireless" should match both Wireless Keyboard and Wireless Mouse
        List<Product> results = productRepository
                .findByNameContainingIgnoreCase("wireless");

        assertThat(results).hasSize(2);
        assertThat(results)
                .extracting(Product::getName)
                .containsExactlyInAnyOrder("Wireless Keyboard", "Wireless Mouse");
    }

    @Test
    void findByNameContainingIgnoreCase_isCaseInsensitive() {
        // uppercase WIRELESS should return same results as lowercase
        List<Product> upper = productRepository.findByNameContainingIgnoreCase("WIRELESS");
        List<Product> lower = productRepository.findByNameContainingIgnoreCase("wireless");

        assertThat(upper).hasSize(lower.size());
    }

    @Test
    void findByNameContainingIgnoreCase_withNoMatch_returnsEmptyList() {
        List<Product> results = productRepository
                .findByNameContainingIgnoreCase("monitor");

        assertThat(results).isEmpty();
    }

    @Test
    void findByNameContainingIgnoreCase_withPartialMatch_returnsCorrectResult() {
        // "hub" should only match USB Hub
        List<Product> results = productRepository
                .findByNameContainingIgnoreCase("hub");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("USB Hub");
    }
}