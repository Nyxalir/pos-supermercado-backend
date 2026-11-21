package com.pos.supermercado.exception;

/**
 * Se lanza cuando la transacción o simulación de pago es rechazada por fondos insuficientes,
 * validación de tarjeta o expiración.
 */
public class PagoRechazadoException extends ReglaNegocioException {
    
    private final String metodo;
    private final String motivo;

    public PagoRechazadoException(String metodo, String motivo) {
        super(String.format("Pago con método %s rechazado: %s", metodo, motivo));
        this.metodo = metodo;
        this.motivo = motivo;
    }

    public String getMetodo() {
        return metodo;
    }

    public String getMotivo() {
        return motivo;
    }
}
