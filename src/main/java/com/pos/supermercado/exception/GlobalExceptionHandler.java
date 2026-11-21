package com.pos.supermercado.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(StockInsuficienteException.class)
    public ResponseEntity<Map<String, Object>> manejarStockInsuficiente(StockInsuficienteException ex) {
        Map<String, Object> error = crearCuerpoError(HttpStatus.CONFLICT, ex.getMessage());
        error.put("codigoBarras", ex.getCodigoBarras());
        error.put("stockDisponible", ex.getStockDisponible());
        error.put("cantidadSolicitada", ex.getCantidadSolicitada());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(PagoRechazadoException.class)
    public ResponseEntity<Map<String, Object>> manejarPagoRechazado(PagoRechazadoException ex) {
        Map<String, Object> error = crearCuerpoError(HttpStatus.BAD_REQUEST, ex.getMessage());
        error.put("metodo", ex.getMetodo());
        error.put("motivo", ex.getMotivo());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(CuponInvalidoException.class)
    public ResponseEntity<Map<String, Object>> manejarCuponInvalido(CuponInvalidoException ex) {
        Map<String, Object> error = crearCuerpoError(HttpStatus.BAD_REQUEST, ex.getMessage());
        error.put("codigo", ex.getCodigo());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> manejarRecursoNoEncontrado(RecursoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(crearCuerpoError(HttpStatus.NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<Map<String, Object>> manejarReglaNegocio(ReglaNegocioException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(crearCuerpoError(HttpStatus.BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidaciones(MethodArgumentNotValidException ex) {
        Map<String, Object> respuesta = crearCuerpoError(HttpStatus.BAD_REQUEST, "Error de validación en los campos enviados");
        Map<String, String> erroresCampos = new HashMap<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            erroresCampos.put(fieldError.getField(), fieldError.getDefaultMessage());
        }
        respuesta.put("errores", erroresCampos);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> manejarGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(crearCuerpoError(HttpStatus.INTERNAL_SERVER_ERROR, "Ha ocurrido un error interno en el servidor: " + ex.getMessage()));
    }

    private Map<String, Object> crearCuerpoError(HttpStatus status, String mensaje) {
        Map<String, Object> cuerpo = new HashMap<>();
        cuerpo.put("timestamp", LocalDateTime.now());
        cuerpo.put("status", status.value());
        cuerpo.put("error", status.getReasonPhrase());
        cuerpo.put("mensaje", mensaje);
        return cuerpo;
    }
}
