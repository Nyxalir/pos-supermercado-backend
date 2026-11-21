package com.pos.supermercado.service;

import com.pos.supermercado.dto.DetalleVentaDTO;
import com.pos.supermercado.dto.ItemVentaDTO;
import com.pos.supermercado.dto.VentaDTO;
import com.pos.supermercado.dto.VentaRequestDTO;
import com.pos.supermercado.exception.CuponInvalidoException;
import com.pos.supermercado.exception.RecursoNoEncontradoException;
import com.pos.supermercado.exception.ReglaNegocioException;
import com.pos.supermercado.mapper.VentaMapper;
import com.pos.supermercado.model.*;
import com.pos.supermercado.repository.CuponRepository;
import com.pos.supermercado.repository.ProductoRepository;
import com.pos.supermercado.repository.VentaRepository;
import com.pos.supermercado.strategy.pago.MetodoPago;
import com.pos.supermercado.strategy.pago.MetodoPagoFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class VentaService {

    private final VentaRepository ventaRepository;
    private final ProductoRepository productoRepository;
    private final CuponRepository cuponRepository;
    private final MetodoPagoFactory metodoPagoFactory;
    private final VentaMapper ventaMapper;

    /**
     * Procesa y liquida una compra completa en el terminal POS bajo una única transacción atómica:
     * 1. Valida productos y existencia de stock suficiente.
     * 2. Descuenta el inventario de cada producto.
     * 3. Aplica cupón de descuento si fue suministrado.
     * 4. Congela precios históricos en DetalleVenta (Snapshot).
     * 5. Procesa el pago mediante el patrón Strategy (Efectivo, Tarjeta o QR).
     * 6. Registra y persiste la venta con su consecutivo de factura.
     */
    @Transactional
    public Venta procesarVenta(VentaRequestDTO request) {
        if (request.getItems() == null || request.getItems().isEmpty()) {
            throw new ReglaNegocioException("La venta debe contener al menos un producto");
        }

        // Crear la entidad Venta
        Venta venta = new Venta();
        venta.setDocumentoCliente(request.getDocumentoCliente().trim());
        venta.setFechaHora(LocalDateTime.now());
        venta.setEstado(EstadoVenta.PAGADA);
        venta.setNumeroFactura(generarNumeroFactura());

        // Aplicar cupón opcional
        if (request.getCodigoCupon() != null && !request.getCodigoCupon().trim().isEmpty()) {
            String codigo = request.getCodigoCupon().trim();
            Cupon cupon = cuponRepository.findByCodigoIgnoreCase(codigo)
                    .orElseThrow(() -> new CuponInvalidoException(codigo, "El cupón no existe"));

            if (!cupon.esValido()) {
                throw new CuponInvalidoException(codigo, "El cupón se encuentra inactivo o ha expirado");
            }
            venta.setCupon(cupon);
        }

        // Procesar líneas de producto
        for (ItemVentaDTO itemDTO : request.getItems()) {
            Producto producto = obtenerProducto(itemDTO);

            if (!Boolean.TRUE.equals(producto.getActivo())) {
                throw new ReglaNegocioException("El producto '" + producto.getNombre() + "' no se encuentra activo");
            }

            // Descuento de stock en memoria (lanza StockInsuficienteException si no hay stock)
            producto.descontarStock(itemDTO.getCantidad());
            productoRepository.save(producto);

            // Crear Detalle de Venta con snapshot del momento de la compra
            DetalleVenta detalle = DetalleVenta.builder()
                    .producto(producto)
                    .nombre(producto.getNombre())
                    .precioUnitario(producto.getPrecio())
                    .ivaPorcentaje(producto.getIvaPorcentaje())
                    .cantidad(itemDTO.getCantidad())
                    .build();

            venta.agregarDetalle(detalle);
        }

        // Calcular subtotales, IVA, descuento y total neto
        venta.calcularTotales();

        // Procesar pago con el patrón Strategy
        MetodoPago estrategiaPago = metodoPagoFactory.obtenerEstrategia(request.getPago().getMetodo());
        Pago pago = estrategiaPago.procesar(venta, request.getPago());
        venta.asignarPago(pago);

        // Guardar la venta con sus detalles y pago en cascada
        return ventaRepository.save(venta);
    }

    @Transactional(readOnly = true)
    public Venta obtenerPorId(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Venta", "id", id));
    }

    @Transactional(readOnly = true)
    public Venta obtenerPorNumeroFactura(String numeroFactura) {
        return ventaRepository.findByNumeroFactura(numeroFactura)
                .orElseThrow(() -> new RecursoNoEncontradoException("Venta", "número de factura", numeroFactura));
    }

    @Transactional(readOnly = true)
    public List<Venta> listarVentasPorCliente(String documentoCliente) {
        return ventaRepository.findByDocumentoClienteOrderByFechaHoraDesc(documentoCliente);
    }

    @Transactional(readOnly = true)
    public List<Venta> listarTodas() {
        return ventaRepository.findAll();
    }

    /**
     * Retorna una venta por su ID ya mapeada a DTO (dentro de la transacción para evitar
     * LazyInitializationException al serializar relaciones).
     */
    @Transactional(readOnly = true)
    public VentaDTO obtenerPorIdDto(Long id) {
        Venta venta = obtenerPorId(id);
        return ventaMapper.toDto(venta);
    }

    /**
     * Retorna una venta por número de factura ya mapeada a DTO (dentro de la transacción).
     */
    @Transactional(readOnly = true)
    public VentaDTO obtenerPorNumeroFacturaDto(String numeroFactura) {
        Venta venta = obtenerPorNumeroFactura(numeroFactura);
        return ventaMapper.toDto(venta);
    }

    /**
     * Retorna las ventas de un cliente ya mapeadas a DTO (dentro de la transacción).
     */
    @Transactional(readOnly = true)
    public List<VentaDTO> listarVentasPorClienteDto(String documentoCliente) {
        List<Venta> ventas = listarVentasPorCliente(documentoCliente);
        return ventaMapper.toDtoList(ventas);
    }

    /**
     * Retorna todas las ventas ya mapeadas a DTO (dentro de la transacción).
     */
    @Transactional(readOnly = true)
    public List<VentaDTO> listarTodasDto() {
        List<Venta> ventas = listarTodas();
        return ventaMapper.toDtoList(ventas);
    }

    /**
     * Anula una factura de venta y revierte el stock de los productos.
     */
    @Transactional
    public Venta anularVenta(Long id) {
        Venta venta = obtenerPorId(id);
        if (venta.getEstado() == EstadoVenta.ANULADA) {
            throw new ReglaNegocioException("La venta ya se encuentra anulada");
        }

        // Reabastecer existencias de cada producto
        for (DetalleVenta det : venta.getDetalles()) {
            if (det.getProducto() != null) {
                Producto producto = det.getProducto();
                producto.reponerStock(det.getCantidad());
                productoRepository.save(producto);
            }
        }

        venta.setEstado(EstadoVenta.ANULADA);
        if (venta.getPago() != null) {
            venta.getPago().setEstado(EstadoPago.RECHAZADO);
        }

        return ventaRepository.save(venta);
    }

    /**
     * Anula una venta y retorna la entidad ya mapeada a DTO.
     */
    @Transactional
    public VentaDTO anularVentaDto(Long id) {
        Venta venta = anularVenta(id);
        return ventaMapper.toDto(venta);
    }

    /**
     * Resuelve el producto por ID o por código de barras.
     */
    private Producto obtenerProducto(ItemVentaDTO itemDTO) {
        if (itemDTO.getProductoId() != null) {
            return productoRepository.findById(itemDTO.getProductoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Producto", "id", itemDTO.getProductoId()));
        } else if (itemDTO.getCodigoBarras() != null && !itemDTO.getCodigoBarras().trim().isEmpty()) {
            return productoRepository.findByCodigoBarras(itemDTO.getCodigoBarras().trim())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Producto", "código de barras", itemDTO.getCodigoBarras()));
        } else {
            throw new ReglaNegocioException("Debe proporcionar el ID o el código de barras del producto");
        }
    }

    /**
     * Genera un número consecutivo único para la factura (ej: POS-000002).
     */
    private synchronized String generarNumeroFactura() {
        long consecutivo = ventaRepository.contarTotalVentas() + 1;
        String numero = String.format("POS-%06d", consecutivo);
        // Validar unicidad por si existen registros previos
        while (ventaRepository.findByNumeroFactura(numero).isPresent()) {
            consecutivo++;
            numero = String.format("POS-%06d", consecutivo);
        }
        return numero;
    }
}
