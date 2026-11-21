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
import java.util.concurrent.ThreadLocalRandom;

@Component
public class PagoTarjeta implements MetodoPago {

    @Override
    public TipoPago getTipo() {
        return TipoPago.TARJETA;
    }

    @Override
    public Pago procesar(Venta venta, SolicitudPagoDTO solicitud) throws PagoRechazadoException {
        if (solicitud == null || solicitud.getNumeroTarjeta() == null || solicitud.getNumeroTarjeta().trim().isEmpty()) {
            throw new PagoRechazadoException("TARJETA", "El número de tarjeta es obligatorio");
        }

        String numero = solicitud.getNumeroTarjeta().replaceAll("[\\s-]+", "");

        if (!numero.matches("^\\d{13,19}$")) {
            throw new PagoRechazadoException("TARJETA", "Longitud de tarjeta inválida (debe contener entre 13 y 19 dígitos)");
        }

        // Validación algoritmo de Luhn
        if (!validarLuhn(numero)) {
            throw new PagoRechazadoException("TARJETA", "Número de tarjeta inválido (falla validación de Luhn)");
        }

        // Regla determinista de rechazo según especificación técnica: tarjetas terminadas en "0000"
        if (numero.endsWith("0000")) {
            throw new PagoRechazadoException("TARJETA", "Transacción rechazada por la entidad bancaria emisora (Fondos insuficientes / Bloqueo)");
        }

        String ultimos4 = numero.substring(numero.length() - 4);
        String codigoAutorizacion = "AUTH-" + ThreadLocalRandom.current().nextInt(100000, 999999);

        return Pago.builder()
                .venta(venta)
                .metodo(TipoPago.TARJETA)
                .monto(venta.getTotal())
                .recibido(venta.getTotal())
                .cambio(BigDecimal.ZERO)
                .estado(EstadoPago.APROBADO)
                .ultimos4(ultimos4)
                .codigoAutorizacion(codigoAutorizacion)
                .fecha(LocalDateTime.now())
                .build();
    }

    /**
     * Algoritmo de Luhn estándar para comprobación de números de tarjeta de crédito/débito.
     */
    private boolean validarLuhn(String numero) {
        int suma = 0;
        boolean alternar = false;
        for (int i = numero.length() - 1; i >= 0; i--) {
            int digito = Character.getNumericValue(numero.charAt(i));
            if (alternar) {
                digito *= 2;
                if (digito > 9) {
                    digito -= 9;
                }
            }
            suma += digito;
            alternar = !alternar;
        }
        return (suma % 10 == 0);
    }
}
