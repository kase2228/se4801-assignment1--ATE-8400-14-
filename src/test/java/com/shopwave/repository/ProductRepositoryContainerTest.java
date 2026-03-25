//ATE/8400/14
package com.shopwave.repository;

import com.shopwave.model.Category;
import com.shopwave.model.Product;
import com.shopwave.shopwave_starter.ShopwaveStarterApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(classes = ShopwaveStarterApplication.class)
class ProductRepositoryContainerTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("shopwave_test")
            .withUsername("test")
            .withPassword("test");

    @DynamicPropertySource
    static void overrideDataSourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.jpa.properties.hibernate.dialect",
                () -> "org.hibernate.dialect.PostgreSQLDialect");
    }

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ProductRepository productRepository;

    private Category category;

    @BeforeEach
    void setUp() {
        productRepository.deleteAll();

        category = entityManager.persistAndFlush(
                Category.builder()
                        .name("Electronics")
                        .description("Electronic devices")
                        .build()
        );

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
        List<Product> results = productRepository
                .findByNameContainingIgnoreCase("wireless");

        assertThat(results).hasSize(2);
        assertThat(results)
                .extracting(Product::getName)
                .containsExactlyInAnyOrder("Wireless Keyboard", "Wireless Mouse");
    }

    @Test
    void findByNameContainingIgnoreCase_isCaseInsensitive() {
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
        List<Product> results = productRepository
                .findByNameContainingIgnoreCase("hub");

        assertThat(results).hasSize(1);
        assertThat(results.get(0).getName()).isEqualTo("USB Hub");
    }
}
