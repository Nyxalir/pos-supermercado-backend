package com.pos.supermercado.service;

import com.pos.supermercado.exception.RecursoNoEncontradoException;
import com.pos.supermercado.exception.ReglaNegocioException;
import com.pos.supermercado.model.Categoria;
import com.pos.supermercado.model.Producto;
import com.pos.supermercado.repository.CategoriaRepository;
import com.pos.supermercado.repository.ProductoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;

    @Transactional(readOnly = true)
    public List<Producto> listarActivos() {
        return productoRepository.findByActivoTrue();
    }

    @Transactional(readOnly = true)
    public List<Producto> listarTodos() {
        return productoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Producto obtenerPorId(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", "id", id));
    }

    @Transactional(readOnly = true)
    public Producto buscarPorCodigoBarras(String codigoBarras) {
        return productoRepository.findByCodigoBarrasAndActivoTrue(codigoBarras)
                .orElseThrow(() -> new RecursoNoEncontradoException("Producto", "código de barras", codigoBarras));
    }

    @Transactional(readOnly = true)
    public List<Producto> buscarPorTermino(String termino) {
        if (termino == null || termino.trim().isEmpty()) {
            return listarActivos();
        }
        return productoRepository.buscarPorNombreOCodigo(termino.trim());
    }

    @Transactional(readOnly = true)
    public List<Producto> listarPorCategoria(Long categoriaId) {
        return productoRepository.findByCategoriaIdAndActivoTrue(categoriaId);
    }

    @Transactional(readOnly = true)
    public List<Producto> listarBajoStock() {
        return productoRepository.findProductosConBajoStock();
    }

    @Transactional
    public Producto guardarProducto(Producto producto, Long categoriaId) {
        if (producto.getId() == null && productoRepository.existsByCodigoBarras(producto.getCodigoBarras())) {
            throw new ReglaNegocioException("Ya existe un producto con el código de barras: " + producto.getCodigoBarras());
        }

        if (categoriaId != null) {
            Categoria categoria = categoriaRepository.findById(categoriaId)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Categoría", "id", categoriaId));
            producto.setCategoria(categoria);
        }

        return productoRepository.save(producto);
    }

    /**
     * Baja lógica del producto para conservar el historial de ventas.
     */
    @Transactional
    public void desactivarProducto(Long id) {
        Producto producto = obtenerPorId(id);
        producto.setActivo(false);
        productoRepository.save(producto);
    }

    @Transactional
    public void reactivarProducto(Long id) {
        Producto producto = obtenerPorId(id);
        producto.setActivo(true);
        productoRepository.save(producto);
    }
}
