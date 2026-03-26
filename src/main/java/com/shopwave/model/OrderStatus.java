//ATE/8400/14.
package com.shopwave.model;

public enum OrderStatus {
    PENDING,
    SHIPPED,
    DELIVERED,
    CANCELLED;

    public boolean isTerminal() {
        return this == DELIVERED || this == CANCELLED;
    }
}