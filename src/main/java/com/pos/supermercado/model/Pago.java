package com.pos.supermercado.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pago")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "venta_id", nullable = false)
    private Venta venta;

    @NotNull(message = "El método de pago es obligatorio")
    @Enumerated(EnumType.STRING)
    @Column(name = "metodo", nullable = false, length = 30)
    private TipoPago metodo;

    @NotNull(message = "El monto a pagar es obligatorio")
    @DecimalMin(value = "0.00", message = "El monto no puede ser negativo")
    @Column(name = "monto", nullable = false, precision = 12, scale = 2)
    private BigDecimal monto;

    @Column(name = "recibido", precision = 12, scale = 2)
    private BigDecimal recibido;

    @Builder.Default
    @Column(name = "cambio", precision = 12, scale = 2)
    private BigDecimal cambio = BigDecimal.ZERO;

    @NotNull(message = "El estado del pago es obligatorio")
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoPago estado = EstadoPago.APROBADO;

    @Column(name = "referencia", length = 100)
    private String referencia;

    @Column(name = "ultimos4", length = 4)
    private String ultimos4;

    @Column(name = "codigo_autorizacion", length = 50)
    private String codigoAutorizacion;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;

    @PrePersist
    protected void onCreate() {
        if (this.fecha == null) {
            this.fecha = LocalDateTime.now();
        }
        if (this.cambio == null) {
            this.cambio = BigDecimal.ZERO;
        }
    }
}
