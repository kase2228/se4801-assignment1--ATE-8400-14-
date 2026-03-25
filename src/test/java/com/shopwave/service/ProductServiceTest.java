package com.shopwave.service;

import com.shopwave.dto.CreateProductRequest;
import com.shopwave.dto.ProductDTO;
import com.shopwave.exception.ProductNotFoundException;
import com.shopwave.mapper.ProductMapper;
import com.shopwave.model.Category;
import com.shopwave.model.Product;
import com.shopwave.repository.CategoryRepository;
import com.shopwave.repository.ProductRepository;
import com.shopwave.shopwave_starter.ShopwaveStarterApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ContextConfiguration;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ContextConfiguration(classes = ShopwaveStarterApplication.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductMapper productMapper;

    @InjectMocks
    private ProductServiceImpl productService;

    private Category category;
    private Product product;
    private CreateProductRequest request;
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .id(1L)
                .name("Electronics")
                .description("Electronic devices")
                .build();

        product = Product.builder()
                .id(1L)
                .name("Wireless Keyboard")
                .description("Compact keyboard")
                .price(new BigDecimal("49.99"))
                .stock(20)
                .category(category)
                .build();

        request = CreateProductRequest.builder()
                .name("Wireless Keyboard")
                .description("Compact keyboard")
                .price(new BigDecimal("49.99"))
                .stock(20)
                .categoryId(1L)
                .build();

        productDTO = ProductDTO.builder()
                .id(1L)
                .name("Wireless Keyboard")
                .description("Compact keyboard")
                .price(new BigDecimal("49.99"))
                .stock(20)
                .categoryId(1L)
                .build();
    }

    // ── createProduct ──────────────────────────────────────────────

    @Test
    void createProduct_happyPath_returnsProductDTO() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(productMapper.toEntity(request, category)).thenReturn(product);
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(productMapper.toDTO(product)).thenReturn(productDTO);

        ProductDTO result = productService.createProduct(request);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Wireless Keyboard");
        assertThat(result.getPrice()).isEqualByComparingTo("49.99");
        assertThat(result.getCategoryId()).isEqualTo(1L);

        verify(categoryRepository, times(1)).findById(1L);
        verify(productRepository, times(1)).save(any(Product.class));
        verify(productMapper, times(1)).toDTO(product);
    }

    @Test
    void createProduct_categoryNotFound_throwsIllegalArgumentException() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.createProduct(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Category not found with id: 1");

        verify(productRepository, never()).save(any());
        verify(productMapper, never()).toDTO(any());
    }

    // ── getProductById ─────────────────────────────────────────────

    @Test
    void getProductById_existingId_returnsProductDTO() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toDTO(product)).thenReturn(productDTO);

        ProductDTO result = productService.getProductById(1L);

        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void getProductById_nonExistingId_throwsProductNotFoundException() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(999L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("999");

        verify(productRepository, times(1)).findById(999L);
    }

    // ── updateStock ────────────────────────────────────────────────

    @Test
    void updateStock_validDelta_updatesStockCorrectly() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productMapper.toDTO(product)).thenReturn(productDTO);

        productService.updateStock(1L, 5);

        assertThat(product.getStock()).isEqualTo(25);
        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void updateStock_negativeDeltaCausesNegativeStock_throwsIllegalArgumentException() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> productService.updateStock(1L, -99999))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Stock cannot go negative");

        verify(productRepository, times(1)).findById(1L);
    }

    @Test
    void updateStock_productNotFound_throwsProductNotFoundException() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.updateStock(999L, 5))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessageContaining("999");
    }
}