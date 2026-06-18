package cl.duoc.gamehub.order.controller.model.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import feign.FeignException;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Captura fallos de las validaciones en DTO
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> erroresValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String campo = ((FieldError) error).getField();
            String mensaje = error.getDefaultMessage();
            errores.put(campo, mensaje);
        });
        return new ResponseEntity<>(errores, HttpStatus.BAD_REQUEST);
    }
    // Captura violaciones de reglas
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> erroresNegocio(RuntimeException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("mensaje", ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
    // Captura bloqueos e interrupciones de OpenFeign
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Map<String, String>> erroresInterservicios(FeignException ex) {
        Map<String, String> error = new HashMap<>();
        error.put("mensaje", "La orden no pudo ser procesada: Fallo o rechazo en validación de dependencias críticas (Catálogo/Inventario).");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}