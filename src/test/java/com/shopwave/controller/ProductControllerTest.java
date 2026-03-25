package com.shopwave.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shopwave.dto.CreateProductRequest;
import com.shopwave.dto.ProductDTO;
import com.shopwave.exception.GlobalExceptionHandler;
import com.shopwave.exception.ProductNotFoundException;
import com.shopwave.service.ProductService;
import com.shopwave.shopwave_starter.ShopwaveStarterApplication;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = ShopwaveStarterApplication.class)
@AutoConfigureMockMvc
@Import(GlobalExceptionHandler.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    private ProductDTO productDTO;
    private CreateProductRequest validRequest;

    @BeforeEach
    void setUp() {
        productDTO = ProductDTO.builder()
                .id(1L)
                .name("Mechanical Keyboard")
                .description("Tactile switches, RGB backlight")
                .price(new BigDecimal("89.99"))
                .stock(15)
                .categoryId(1L)
                .build();

        validRequest = CreateProductRequest.builder()
                .name("Mechanical Keyboard")
                .description("Tactile switches, RGB backlight")
                .price(new BigDecimal("89.99"))
                .stock(15)
                .categoryId(1L)
                .build();
    }

    @Test
    void getAllProducts_returnsOkWithPaginatedBody() throws Exception {
        PageImpl<ProductDTO> page = new PageImpl<>(
                List.of(productDTO),
                PageRequest.of(0, 10),
                1
        );

        when(productService.getAllProducts(any())).thenReturn(page);

        mockMvc.perform(get("/api/products")
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Mechanical Keyboard"))
                .andExpect(jsonPath("$.content[0].price").value(89.99))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.totalPages").value(1));
    }

    @Test
    void getProductById_existingId_returnsOkWithProduct() throws Exception {
        when(productService.getProductById(1L)).thenReturn(productDTO);

        mockMvc.perform(get("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Mechanical Keyboard"))
                .andExpect(jsonPath("$.stock").value(15));
    }

    @Test
    void getProductById_nonExistingId_returns404WithErrorJson() throws Exception {
        when(productService.getProductById(999L))
                .thenThrow(new ProductNotFoundException(999L));

        mockMvc.perform(get("/api/products/999")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Product not found with id: 999"))
                .andExpect(jsonPath("$.path").value("/api/products/999"))
                .andExpect(jsonPath("$.timestamp").exists());
    }

    @Test
    void createProduct_validRequest_returnsCreatedWithProductDTO() throws Exception {
        when(productService.createProduct(any())).thenReturn(productDTO);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Mechanical Keyboard"));
    }

    @Test
    void createProduct_invalidRequest_returns400WithValidationErrors() throws Exception {
        CreateProductRequest invalidRequest = CreateProductRequest.builder()
                .name("")
                .description("Test description")
                .price(new BigDecimal("-10"))
                .stock(-1)
                .categoryId(999L)
                .build();

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.timestamp").exists());
    }
}