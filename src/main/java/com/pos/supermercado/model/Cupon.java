package com.pos.supermercado.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "cupon")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Cupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El código de cupón es obligatorio")
    @Column(name = "codigo", nullable = false, unique = true, length = 50)
    private String codigo;

    @NotNull(message = "El porcentaje de descuento es obligatorio")
    @DecimalMin(value = "0.01", message = "El descuento debe ser mayor a 0%")
    @DecimalMax(value = "100.00", message = "El descuento no puede superar el 100%")
    @Column(name = "descuento_porcentaje", nullable = false, precision = 5, scale = 2)
    private BigDecimal descuentoPorcentaje;

    @Builder.Default
    @Column(name = "activo", nullable = false)
    private Boolean activo = true;

    @Column(name = "fecha_expiracion")
    private LocalDateTime fechaExpiracion;

    /**
     * Valida si el cupón está activo y no ha expirado.
     */
    public boolean esValido() {
        if (!Boolean.TRUE.equals(this.activo)) {
            return false;
        }
        return this.fechaExpiracion == null || this.fechaExpiracion.isAfter(LocalDateTime.now());
    }
}
