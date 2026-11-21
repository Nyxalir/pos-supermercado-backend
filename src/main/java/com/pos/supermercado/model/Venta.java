package com.pos.supermercado.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "venta")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Venta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "El número de factura es obligatorio")
    @Column(name = "numero_factura", nullable = false, unique = true, length = 50)
    private String numeroFactura;

    /**
     * Cédula / Documento de identidad del comprador digitado en caja por el cajero.
     * Enfoque directo sin necesidad de registro previo en base de datos.
     */
    @NotBlank(message = "La identificación del cliente (cédula) es obligatoria")
    @Column(name = "documento_cliente", nullable = false, length = 20)
    private String documentoCliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cupon_id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Cupon cupon;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @NotNull
    @DecimalMin(value = "0.00")
    @Builder.Default
    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @NotNull
    @DecimalMin(value = "0.00")
    @Builder.Default
    @Column(name = "total_iva", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalIva = BigDecimal.ZERO;

    @NotNull
    @DecimalMin(value = "0.00")
    @Builder.Default
    @Column(name = "descuento", nullable = false, precision = 12, scale = 2)
    private BigDecimal descuento = BigDecimal.ZERO;

    @NotNull
    @DecimalMin(value = "0.00")
    @Builder.Default
    @Column(name = "total", nullable = false, precision = 12, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Builder.Default
    @Column(name = "estado", nullable = false, length = 20)
    private EstadoVenta estado = EstadoVenta.PAGADA;

    @Builder.Default
    @OneToMany(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleVenta> detalles = new ArrayList<>();

    @OneToOne(mappedBy = "venta", cascade = CascadeType.ALL, orphanRemoval = true)
    private Pago pago;

    @PrePersist
    protected void onCreate() {
        if (this.fechaHora == null) {
            this.fechaHora = LocalDateTime.now();
        }
    }

    /**
     * Asocia un detalle a la venta asegurando la coherencia bidireccional.
     */
    public void agregarDetalle(DetalleVenta detalle) {
        if (detalle != null) {
            this.detalles.add(detalle);
            detalle.setVenta(this);
        }
    }

    /**
     * Asocia el pago a la venta de forma bidireccional.
     */
    public void asignarPago(Pago pago) {
        this.pago = pago;
        if (pago != null) {
            pago.setVenta(this);
        }
    }

    /**
     * Calcula los subtotales, desglose de IVA, descuento por cupón y total final.
     * Los precios de venta al público ya incluyen IVA, por lo que:
     * base = precio / (1 + (iva / 100))
     * iva_monto = total_linea - base
     */
    public void calcularTotales() {
        BigDecimal baseAcumulada = BigDecimal.ZERO;
        BigDecimal ivaAcumulado = BigDecimal.ZERO;
        BigDecimal totalBruto = BigDecimal.ZERO;

        for (DetalleVenta det : this.detalles) {
            det.recalcularTotalLinea();
            BigDecimal totalLinea = det.getTotalLinea();
            totalBruto = totalBruto.add(totalLinea);

            BigDecimal tasaIva = det.getIvaPorcentaje().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            BigDecimal factor = BigDecimal.ONE.add(tasaIva);
            BigDecimal baseLinea = totalLinea.divide(factor, 2, RoundingMode.HALF_UP);
            BigDecimal ivaLinea = totalLinea.subtract(baseLinea);

            baseAcumulada = baseAcumulada.add(baseLinea);
            ivaAcumulado = ivaAcumulado.add(ivaLinea);
        }

        this.subtotal = baseAcumulada.setScale(2, RoundingMode.HALF_UP);
        this.totalIva = ivaAcumulado.setScale(2, RoundingMode.HALF_UP);

        // Descuento por cupón
        if (this.cupon != null && this.cupon.esValido()) {
            BigDecimal factorDesc = this.cupon.getDescuentoPorcentaje()
                    .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            this.descuento = totalBruto.multiply(factorDesc).setScale(2, RoundingMode.HALF_UP);
        } else {
            this.descuento = BigDecimal.ZERO;
        }

        this.total = totalBruto.subtract(this.descuento).setScale(2, RoundingMode.HALF_UP);
    }
}
