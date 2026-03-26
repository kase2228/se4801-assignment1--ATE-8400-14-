//ATE/8400/14
package com.shopwave.dto;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(onlyExplicitlyIncluded = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class StockUpdateRequest {

    @NotNull(message = "Delta must not be null")
    @ToString.Include
    private Integer delta;
}