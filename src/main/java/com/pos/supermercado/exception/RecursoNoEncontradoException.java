package com.pos.supermercado.exception;

/**
 * Se lanza cuando un recurso solicitado (producto, venta, categoría, etc.) no existe en el sistema.
 */
public class RecursoNoEncontradoException extends ReglaNegocioException {
    
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }

    public RecursoNoEncontradoException(String entidad, String identificador, Object valor) {
        super(String.format("%s con %s '%s' no encontrado", entidad, identificador, valor));
    }
}
