package com.pos.supermercado.repository;

import com.pos.supermercado.model.Cupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CuponRepository extends JpaRepository<Cupon, Long> {
    
    Optional<Cupon> findByCodigoIgnoreCase(String codigo);
    
    Optional<Cupon> findByCodigoIgnoreCaseAndActivoTrue(String codigo);
    
    boolean existsByCodigoIgnoreCase(String codigo);
}
