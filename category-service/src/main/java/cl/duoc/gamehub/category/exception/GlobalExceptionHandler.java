package cl.duoc.gamehub.category.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Si el usuario deja el nombre vacío
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

    // Si salta un error de negocio
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> erroresDeNegocio(RuntimeException ex) {
        Map<String, String> errorJson = new HashMap<>();
        errorJson.put("mensaje", ex.getMessage());

        return new ResponseEntity<>(errorJson, HttpStatus.BAD_REQUEST);
    }
}