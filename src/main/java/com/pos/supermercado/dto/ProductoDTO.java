package com.pos.supermercado.dto;

import java.math.BigDecimal;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductoDTO {

    private Long id;
    private String codigoBarras;
    private String nombre;
    private Long categoriaId;
    private String categoriaNombre;
    private BigDecimal precio;
    private BigDecimal ivaPorcentaje;
    private Integer stock;
    private Integer stockMinimo;
    private Boolean activo;
}
