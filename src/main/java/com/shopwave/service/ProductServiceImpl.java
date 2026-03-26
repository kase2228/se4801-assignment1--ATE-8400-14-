//ATE/8400/14
package com.shopwave.service;

import com.shopwave.dto.CreateProductRequest;
import com.shopwave.dto.ProductDTO;
import com.shopwave.exception.ProductNotFoundException;
import com.shopwave.mapper.ProductMapper;
import com.shopwave.model.Category;
import com.shopwave.model.Product;
import com.shopwave.repository.CategoryRepository;
import com.shopwave.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductDTO createProduct(CreateProductRequest request) {
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException(
                        "Category not found with id: " + request.getCategoryId()));

        Product product = productMapper.toEntity(request, category);
        Product saved = productRepository.save(product);

        return productMapper.toDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductDTO> getAllProducts(Pageable pageable) {
        return productRepository.findAll(pageable)
                .map(productMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        return productMapper.toDTO(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductDTO> searchProducts(String keyword, BigDecimal maxPrice) {
        if (keyword != null && maxPrice != null) {
            return productRepository
                    .findByNameContainingIgnoreCaseAndPriceLessThanEqual(keyword, maxPrice)
                    .stream()
                    .map(productMapper::toDTO)
                    .toList();
        }

        if (keyword != null) {
            return productRepository
                    .findByNameContainingIgnoreCase(keyword)
                    .stream()
                    .map(productMapper::toDTO)
                    .toList();
        }

        if (maxPrice != null) {
            return productRepository
                    .findByPriceLessThanEqual(maxPrice)
                    .stream()
                    .map(productMapper::toDTO)
                    .toList();
        }

        return List.of();
    }

    @Override
    public ProductDTO updateStock(Long id, int delta) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));

        int newStock = product.getStock() + delta;

        if (newStock < 0) {
            throw new IllegalArgumentException(
                    "Stock cannot go negative. Current: " + product.getStock() +
                            ", delta: " + delta);
        }

        product.setStock(newStock);
        return productMapper.toDTO(product);
    }
}