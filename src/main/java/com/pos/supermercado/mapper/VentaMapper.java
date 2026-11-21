package com.pos.supermercado.mapper;

import com.pos.supermercado.model.DetalleVenta;
import com.pos.supermercado.model.Producto;
import com.pos.supermercado.model.Venta;
import com.pos.supermercado.dto.DetalleVentaDTO;
import com.pos.supermercado.dto.ProductoDTO;
import com.pos.supermercado.dto.VentaDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class VentaMapper {

    public VentaDTO toDto(Venta venta) {
        if (venta == null) {
            return null;
        }
        return VentaDTO.builder()
                .id(venta.getId())
                .numeroFactura(venta.getNumeroFactura())
                .documentoCliente(venta.getDocumentoCliente())
                .fechaHora(venta.getFechaHora())
                .subtotal(venta.getSubtotal())
                .totalIva(venta.getTotalIva())
                .descuento(venta.getDescuento())
                .total(venta.getTotal())
                .estado(venta.getEstado() != null ? venta.getEstado().name() : null)
                .metodoPago(venta.getPago() != null && venta.getPago().getMetodo() != null
                        ? venta.getPago().getMetodo().name() : null)
                .detalles(toDetalleDtoList(venta.getDetalles()))
                .build();
    }

    public List<VentaDTO> toDtoList(List<Venta> ventas) {
        if (ventas == null) {
            return List.of();
        }
        return ventas.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    private List<DetalleVentaDTO> toDetalleDtoList(List<DetalleVenta> detalles) {
        if (detalles == null) {
            return List.of();
        }
        return detalles.stream()
                .map(this::toDetalleDto)
                .collect(Collectors.toList());
    }

    private DetalleVentaDTO toDetalleDto(DetalleVenta detalle) {
        if (detalle == null) {
            return null;
        }
        Producto producto = detalle.getProducto();
        String categoriaNombre = null;
        Long categoriaId = null;
        if (producto != null && producto.getCategoria() != null) {
            categoriaNombre = producto.getCategoria().getNombre();
            categoriaId = producto.getCategoria().getId();
        }
        return DetalleVentaDTO.builder()
                .id(detalle.getId())
                .nombre(detalle.getNombre())
                .cantidad(detalle.getCantidad())
                .precioUnitario(detalle.getPrecioUnitario())
                .totalLinea(detalle.getTotalLinea())
                .categoriaNombre(categoriaNombre)
                .build();
    }

    public ProductoDTO toProductoDto(Producto producto) {
        if (producto == null) {
            return null;
        }
        return ProductoDTO.builder()
                .id(producto.getId())
                .codigoBarras(producto.getCodigoBarras())
                .nombre(producto.getNombre())
                .categoriaId(producto.getCategoria() != null ? producto.getCategoria().getId() : null)
                .categoriaNombre(producto.getCategoria() != null ? producto.getCategoria().getNombre() : null)
                .precio(producto.getPrecio())
                .ivaPorcentaje(producto.getIvaPorcentaje())
                .stock(producto.getStock())
                .stockMinimo(producto.getStockMinimo())
                .activo(producto.getActivo())
                .build();
    }

    public List<ProductoDTO> toProductoDtoList(List<Producto> productos) {
        if (productos == null) {
            return List.of();
        }
        return productos.stream()
                .map(this::toProductoDto)
                .collect(Collectors.toList());
    }
}
