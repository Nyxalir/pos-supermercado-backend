package com.pos.supermercado.dto;

import com.pos.supermercado.model.TipoPago;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudPagoDTO {

    @NotNull(message = "El método de pago es obligatorio")
    private TipoPago metodo;

    // Solo requerido para pago en EFECTIVO
    private BigDecimal recibido;

    // Campos para simulación de TARJETA
    private String numeroTarjeta;
    private String titular;
    private String fechaExpiracion;
    private String cvv;
    private Integer cuotas;

    // Campo opcional para simulación de QR
    private String referenciaQR;
}
