package com.pos.supermercado.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuponDTO {

    private Long id;
    private String codigo;
    private BigDecimal descuentoPorcentaje;
    private Boolean activo;
    private LocalDateTime fechaExpiracion;
}
