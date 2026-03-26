//ATE/8400/14
package com.shopwave.exception;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(Long id) {
        super("Product not found with id: " + id);
    }

    public ProductNotFoundException(String name) {
        super("Product not found with name: " + name);
    }
}