package com.pos.supermercado.repository;

import com.pos.supermercado.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    Optional<Producto> findByCodigoBarras(String codigoBarras);

    Optional<Producto> findByCodigoBarrasAndActivoTrue(String codigoBarras);

    boolean existsByCodigoBarras(String codigoBarras);

    List<Producto> findByActivoTrue();

    List<Producto> findByCategoriaIdAndActivoTrue(Long categoriaId);

    @Query("SELECT p FROM Producto p WHERE p.activo = true AND (" +
           "LOWER(p.nombre) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "p.codigoBarras LIKE CONCAT('%', :query, '%'))")
    List<Producto> buscarPorNombreOCodigo(@Param("query") String query);

    @Query("SELECT p FROM Producto p WHERE p.activo = true AND p.stock <= p.stockMinimo")
    List<Producto> findProductosConBajoStock();
}
