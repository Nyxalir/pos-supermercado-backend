package com.pos.supermercado.exception;

/**
 * Se lanza cuando un cupón de descuento no existe, está inactivo o ha expirado.
 */
public class CuponInvalidoException extends ReglaNegocioException {
    
    private final String codigo;

    public CuponInvalidoException(String codigo, String motivo) {
        super(String.format("Cupón '%s' inválido: %s", codigo, motivo));
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
