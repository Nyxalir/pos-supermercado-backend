package com.pos.supermercado.repository;

import com.pos.supermercado.model.Venta;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {

    @Override
    @EntityGraph(attributePaths = {"detalles", "detalles.producto", "detalles.producto.categoria", "pago", "cupon"})
    Optional<Venta> findById(Long id);

    @Override
    @EntityGraph(attributePaths = {"detalles", "detalles.producto", "detalles.producto.categoria", "pago", "cupon"})
    List<Venta> findAll();

    @EntityGraph(attributePaths = {"detalles", "detalles.producto", "detalles.producto.categoria", "pago", "cupon"})
    Optional<Venta> findByNumeroFactura(String numeroFactura);

    @EntityGraph(attributePaths = {"detalles", "detalles.producto", "detalles.producto.categoria", "pago", "cupon"})
    List<Venta> findByDocumentoClienteOrderByFechaHoraDesc(String documentoCliente);

    @EntityGraph(attributePaths = {"detalles", "detalles.producto", "detalles.producto.categoria", "pago", "cupon"})
    List<Venta> findByFechaHoraBetweenOrderByFechaHoraDesc(LocalDateTime inicio, LocalDateTime fin);

    @Query("SELECT COUNT(v) FROM Venta v")
    long contarTotalVentas();

    @Query("SELECT MAX(v.id) FROM Venta v")
    Long obtenerUltimoId();
}
