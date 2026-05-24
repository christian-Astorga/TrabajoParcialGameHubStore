package cl.duoc.gamehub.inventory.exception;

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

    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Map<String, String>> errorDeComunicacionFeign(FeignException ex) {
        Map<String, String> errorJson = new HashMap<>();
        if (ex.status() == 400 || ex.status() == 404) {
            errorJson.put("mensaje", "Error de validación: El producto seleccionado no existe en el catálogo de GameHub.");
        } else {
            errorJson.put("mensaje", "Error temporal de comunicación con el servicio de productos.");
        }
        return new ResponseEntity<>(errorJson, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> erroresDeNegocio(RuntimeException ex) {
        Map<String, String> errorJson = new HashMap<>();
        errorJson.put("mensaje", ex.getMessage());
        return new ResponseEntity<>(errorJson, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> erroresDeValidacion(MethodArgumentNotValidException ex) {
        Map<String, String> errores = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String campo = ((FieldError) error).getField();
            String mensaje = error.getDefaultMessage();
            errores.put(campo, mensaje);
        });
        return new ResponseEntity<>(errores, HttpStatus.BAD_REQUEST);
    }
}