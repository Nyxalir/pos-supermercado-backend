package com.pos.supermercado.controller;

import com.pos.supermercado.dto.CuponDTO;
import com.pos.supermercado.exception.RecursoNoEncontradoException;
import com.pos.supermercado.model.Cupon;
import com.pos.supermercado.service.CuponService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cupones")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CuponController {

    private final CuponService cuponService;

    @GetMapping
    public ResponseEntity<List<CuponDTO>> listarTodos() {
        List<CuponDTO> cupones = cuponService.listarTodos().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(cupones);
    }

    @GetMapping("/validar/{codigo}")
    public ResponseEntity<CuponDTO> validarCupon(@PathVariable String codigo) {
        return ResponseEntity.ok(toDto(cuponService.validarCupon(codigo)));
    }

    @PostMapping
    public ResponseEntity<CuponDTO> crearCupon(@Valid @RequestBody Cupon cupon) {
        Cupon guardado = cuponService.guardarCupon(cupon);
        return ResponseEntity.status(HttpStatus.CREATED).body(toDto(guardado));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CuponDTO> actualizarCupon(@PathVariable Long id, @Valid @RequestBody Cupon cupon) {
        cupon.setId(id);
        Cupon actualizado = cuponService.guardarCupon(cupon);
        return ResponseEntity.ok(toDto(actualizado));
    }

    @PatchMapping("/{id}/desactivar")
    public ResponseEntity<Void> desactivarCupon(@PathVariable Long id) {
        Cupon cupon = cuponService.obtenerPorId(id);
        cupon.setActivo(false);
        cuponService.guardarCupon(cupon);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reactivar")
    public ResponseEntity<Void> reactivarCupon(@PathVariable Long id) {
        Cupon cupon = cuponService.obtenerPorId(id);
        cupon.setActivo(true);
        cuponService.guardarCupon(cupon);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCupon(@PathVariable Long id) {
        cuponService.eliminarCupon(id);
        return ResponseEntity.noContent().build();
    }

    private CuponDTO toDto(Cupon cupon) {
        return CuponDTO.builder()
                .id(cupon.getId())
                .codigo(cupon.getCodigo())
                .descuentoPorcentaje(cupon.getDescuentoPorcentaje())
                .activo(cupon.getActivo())
                .fechaExpiracion(cupon.getFechaExpiracion())
                .build();
    }
}
