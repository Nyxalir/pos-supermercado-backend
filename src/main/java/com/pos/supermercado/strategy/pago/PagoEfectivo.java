package com.pos.supermercado.strategy.pago;

import com.pos.supermercado.dto.SolicitudPagoDTO;
import com.pos.supermercado.exception.PagoRechazadoException;
import com.pos.supermercado.model.EstadoPago;
import com.pos.supermercado.model.Pago;
import com.pos.supermercado.model.TipoPago;
import com.pos.supermercado.model.Venta;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Component
public class PagoEfectivo implements MetodoPago {

    @Override
    public TipoPago getTipo() {
        return TipoPago.EFECTIVO;
    }

    @Override
    public Pago procesar(Venta venta, SolicitudPagoDTO solicitud) throws PagoRechazadoException {
        BigDecimal totalVenta = venta.getTotal();
        BigDecimal dineroRecibido = solicitud != null ? solicitud.getRecibido() : null;

        if (dineroRecibido == null) {
            throw new PagoRechazadoException("EFECTIVO", "Debe ingresar el monto recibido en efectivo");
        }

        if (dineroRecibido.compareTo(totalVenta) < 0) {
            throw new PagoRechazadoException(
                    "EFECTIVO",
                    String.format("Monto recibido ($%s) es menor al total a pagar ($%s)", dineroRecibido, totalVenta)
            );
        }

        BigDecimal cambio = dineroRecibido.subtract(totalVenta).setScale(2, RoundingMode.HALF_UP);

        return Pago.builder()
                .venta(venta)
                .metodo(TipoPago.EFECTIVO)
                .monto(totalVenta)
                .recibido(dineroRecibido)
                .cambio(cambio)
                .estado(EstadoPago.APROBADO)
                .fecha(LocalDateTime.now())
                .build();
    }
}
