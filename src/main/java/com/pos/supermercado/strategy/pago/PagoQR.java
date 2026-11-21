package com.pos.supermercado.strategy.pago;

import com.pos.supermercado.dto.SolicitudPagoDTO;
import com.pos.supermercado.exception.PagoRechazadoException;
import com.pos.supermercado.model.EstadoPago;
import com.pos.supermercado.model.Pago;
import com.pos.supermercado.model.TipoPago;
import com.pos.supermercado.model.Venta;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class PagoQR implements MetodoPago {

    @Override
    public TipoPago getTipo() {
        return TipoPago.QR;
    }

    @Override
    public Pago procesar(Venta venta, SolicitudPagoDTO solicitud) throws PagoRechazadoException {
        // Genera referencia única o reutiliza la provista en la solicitud
        String referencia = (solicitud != null && solicitud.getReferenciaQR() != null && !solicitud.getReferenciaQR().trim().isEmpty())
                ? solicitud.getReferenciaQR().trim()
                : "QR-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        String codigoAutorizacion = "QR-OK-" + ThreadLocalRandom.current().nextInt(100000, 999999);

        return Pago.builder()
                .venta(venta)
                .metodo(TipoPago.QR)
                .monto(venta.getTotal())
                .recibido(venta.getTotal())
                .cambio(BigDecimal.ZERO)
                .estado(EstadoPago.APROBADO)
                .referencia(referencia)
                .codigoAutorizacion(codigoAutorizacion)
                .fecha(LocalDateTime.now())
                .build();
    }
}
