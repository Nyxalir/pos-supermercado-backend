package com.pos.supermercado.strategy.pago;

import com.pos.supermercado.model.TipoPago;
import org.springframework.stereotype.Component;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Fábrica y despachador de estrategias de pago.
 * Mapea cada TipoPago a su correspondiente implementación de MetodoPago.
 */
@Component
public class MetodoPagoFactory {

    private final Map<TipoPago, MetodoPago> estrategias = new EnumMap<>(TipoPago.class);

    public MetodoPagoFactory(List<MetodoPago> metodos) {
        for (MetodoPago metodo : metodos) {
            estrategias.put(metodo.getTipo(), metodo);
        }
    }

    /**
     * Obtiene la estrategia correspondiente al método de pago solicitado.
     */
    public MetodoPago obtenerEstrategia(TipoPago tipo) {
        MetodoPago estrategia = estrategias.get(tipo);
        if (estrategia == null) {
            throw new IllegalArgumentException("No existe estrategia implementada para el método de pago: " + tipo);
        }
        return estrategia;
    }
}
