package com.pos.supermercado.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VentaDTO {

    private Long id;
    private String numeroFactura;
    private String documentoCliente;
    private LocalDateTime fechaHora;
    private BigDecimal subtotal;
    private BigDecimal totalIva;
    private BigDecimal descuento;
    private BigDecimal total;
    private String estado;
    private String metodoPago;
    private List<DetalleVentaDTO> detalles;
}
