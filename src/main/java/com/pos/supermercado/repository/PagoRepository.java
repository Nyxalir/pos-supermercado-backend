package com.pos.supermercado.repository;

import com.pos.supermercado.model.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    Optional<Pago> findByVentaId(Long ventaId);

    Optional<Pago> findByReferencia(String referencia);
}
