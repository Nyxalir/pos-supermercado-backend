package com.pos.supermercado.service;

import com.pos.supermercado.exception.CuponInvalidoException;
import com.pos.supermercado.exception.RecursoNoEncontradoException;
import com.pos.supermercado.model.Cupon;
import com.pos.supermercado.repository.CuponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CuponService {

    private final CuponRepository cuponRepository;

    @Transactional(readOnly = true)
    public Cupon validarCupon(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            throw new CuponInvalidoException(codigo, "El código del cupón no puede estar vacío");
        }

        Cupon cupon = cuponRepository.findByCodigoIgnoreCase(codigo.trim())
                .orElseThrow(() -> new RecursoNoEncontradoException("Cupón", "código", codigo));

        if (!cupon.esValido()) {
            throw new CuponInvalidoException(codigo, "El cupón se encuentra vencido o inactivo");
        }

        return cupon;
    }

    @Transactional(readOnly = true)
    public List<Cupon> listarTodos() {
        return cuponRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Cupon obtenerPorId(Long id) {
        return cuponRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cupón", "id", id));
    }

    @Transactional
    public Cupon guardarCupon(Cupon cupon) {
        return cuponRepository.save(cupon);
    }

    @Transactional
    public void eliminarCupon(Long id) {
        Cupon cupon = obtenerPorId(id);
        cuponRepository.delete(cupon);
    }
}
