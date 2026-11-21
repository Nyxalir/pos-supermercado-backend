package com.pos.supermercado.controller;

import com.pos.supermercado.dto.VentaDTO;
import com.pos.supermercado.dto.VentaRequestDTO;
import com.pos.supermercado.model.Venta;
import com.pos.supermercado.service.VentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class VentaController {

    private final VentaService ventaService;

    /**
     * Endpoint principal de checkout del POS.
     * Recibe la cédula del comprador, el listado de productos y los datos del pago simulado.
     */
    @PostMapping
    public ResponseEntity<VentaDTO> procesarVenta(@Valid @RequestBody VentaRequestDTO request) {
        Venta ventaRealizada = ventaService.procesarVenta(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ventaService.obtenerPorIdDto(ventaRealizada.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentaDTO> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ventaService.obtenerPorIdDto(id));
    }

    @GetMapping("/factura/{numeroFactura}")
    public ResponseEntity<VentaDTO> obtenerPorNumeroFactura(@PathVariable String numeroFactura) {
        return ResponseEntity.ok(ventaService.obtenerPorNumeroFacturaDto(numeroFactura));
    }

    @GetMapping("/cliente/{documentoCliente}")
    public ResponseEntity<List<VentaDTO>> listarPorCliente(@PathVariable String documentoCliente) {
        return ResponseEntity.ok(ventaService.listarVentasPorClienteDto(documentoCliente));
    }

    @GetMapping
    public ResponseEntity<List<VentaDTO>> listarTodas() {
        return ResponseEntity.ok(ventaService.listarTodasDto());
    }

    @PostMapping("/{id}/anular")
    public ResponseEntity<VentaDTO> anularVenta(@PathVariable Long id) {
        return ResponseEntity.ok(ventaService.anularVentaDto(id));
    }
}
