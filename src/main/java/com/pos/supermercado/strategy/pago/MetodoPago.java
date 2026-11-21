package com.pos.supermercado.strategy.pago;

import com.pos.supermercado.dto.SolicitudPagoDTO;
import com.pos.supermercado.exception.PagoRechazadoException;
import com.pos.supermercado.model.Pago;
import com.pos.supermercado.model.TipoPago;
import com.pos.supermercado.model.Venta;

/**
 * Interfaz base del patrón Strategy para el procesamiento de pagos.
 * Cada medio de pago implementa su propia lógica de validación y liquidación.
 */
public interface MetodoPago {

    /**
     * Tipo de medio de pago soportado por esta estrategia.
     */
    TipoPago getTipo();

    /**
     * Procesa la simulación de cobro para una venta y retorna la entidad Pago construida.
     *
     * @param venta Venta sobre la cual se aplica el pago.
     * @param solicitud Datos específicos enviados desde la terminal POS (recibido, tarjeta, QR).
     * @return Pago validado y listo para persistir.
     * @throws PagoRechazadoException si las validaciones financieras o de saldo fallan.
     */
    Pago procesar(Venta venta, SolicitudPagoDTO solicitud) throws PagoRechazadoException;
}
