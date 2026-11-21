package com.pos.supermercado.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VentaRequestDTO {

    @NotBlank(message = "La cédula / documento del cliente es obligatoria")
    private String documentoCliente;

    private String codigoCupon;

    @NotEmpty(message = "La venta debe contener al menos un producto")
    @Valid
    private List<ItemVentaDTO> items;

    @NotNull(message = "Los datos del pago son obligatorios")
    @Valid
    private SolicitudPagoDTO pago;
}
