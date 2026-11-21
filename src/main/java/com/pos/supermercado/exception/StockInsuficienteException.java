package com.pos.supermercado.exception;

/**
 * Se lanza cuando la cantidad solicitada de un producto supera las existencias disponibles.
 */
public class StockInsuficienteException extends ReglaNegocioException {
    
    private final String codigoBarras;
    private final int stockDisponible;
    private final int cantidadSolicitada;

    public StockInsuficienteException(String nombreProducto, String codigoBarras, int stockDisponible, int cantidadSolicitada) {
        super(String.format("Stock insuficiente para '%s' (Código: %s). Disponible: %d, Solicitado: %d",
                nombreProducto, codigoBarras, stockDisponible, cantidadSolicitada));
        this.codigoBarras = codigoBarras;
        this.stockDisponible = stockDisponible;
        this.cantidadSolicitada = cantidadSolicitada;
    }

    public String getCodigoBarras() {
        return codigoBarras;
    }

    public int getStockDisponible() {
        return stockDisponible;
    }

    public int getCantidadSolicitada() {
        return cantidadSolicitada;
    }
}
