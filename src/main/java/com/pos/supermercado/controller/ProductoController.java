package com.pos.supermercado.controller;

import com.pos.supermercado.dto.ProductoDTO;
import com.pos.supermercado.mapper.VentaMapper;
import com.pos.supermercado.model.Producto;
import com.pos.supermercado.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ProductoController {

    private final ProductoService productoService;
    private final VentaMapper ventaMapper;

    @GetMapping
    public ResponseEntity<List<ProductoDTO>> listarActivos(@RequestParam(required = false) String buscar) {
        if (buscar != null && !buscar.trim().isEmpty()) {
            return ResponseEntity.ok(ventaMapper.toProductoDtoList(productoService.buscarPorTermino(buscar)));
        }
        return ResponseEntity.ok(ventaMapper.toProductoDtoList(productoService.listarActivos()));
    }

    @GetMapping("/todos")
    public ResponseEntity<List<ProductoDTO>> listarTodos() {
        return ResponseEntity.ok(ventaMapper.toProductoDtoList(productoService.listarTodos()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ventaMapper.toProductoDto(productoService.obtenerPorId(id)));
    }

    @GetMapping("/codigo/{codigoBarras}")
    public ResponseEntity<ProductoDTO> buscarPorCodigoBarras(@PathVariable String codigoBarras) {
        return ResponseEntity.ok(ventaMapper.toProductoDto(productoService.buscarPorCodigoBarras(codigoBarras)));
    }

    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<List<ProductoDTO>> listarPorCategoria(@PathVariable Long categoriaId) {
        return ResponseEntity.ok(ventaMapper.toProductoDtoList(productoService.listarPorCategoria(categoriaId)));
    }

    @GetMapping("/bajo-stock")
    public ResponseEntity<List<ProductoDTO>> listarBajoStock() {
        return ResponseEntity.ok(ventaMapper.toProductoDtoList(productoService.listarBajoStock()));
    }

    @PostMapping
    public ResponseEntity<ProductoDTO> crearProducto(
            @Valid @RequestBody Producto producto,
            @RequestParam(required = false) Long categoriaId) {
        Producto nuevo = productoService.guardarProducto(producto, categoriaId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ventaMapper.toProductoDto(nuevo));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoDTO> actualizarProducto(
            @PathVariable Long id,
            @Valid @RequestBody Producto producto,
            @RequestParam(required = false) Long categoriaId) {
        producto.setId(id);
        Producto actualizado = productoService.guardarProducto(producto, categoriaId);
        return ResponseEntity.ok(ventaMapper.toProductoDto(actualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivarProducto(@PathVariable Long id) {
        productoService.desactivarProducto(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<Void> reactivarProducto(@PathVariable Long id) {
        productoService.reactivarProducto(id);
        return ResponseEntity.noContent().build();
    }
}
