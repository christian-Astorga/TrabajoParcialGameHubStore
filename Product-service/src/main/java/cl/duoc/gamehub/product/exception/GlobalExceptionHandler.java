package cl.duoc.gamehub.product.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import feign.FeignException; // <- Asegúrate de que se importe esto
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    // Este metodo va a atrapar cuando Feign falle porque la categoría no existe
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<Map<String, String>> errorDeComunicacionFeign(FeignException ex) {
        Map<String, String> errorJson = new HashMap<>();

        if (ex.status() == 400) {
            errorJson.put("mensaje", "Error de validación: La categoría seleccionada no existe en el catálogo.");
        } else {
            errorJson.put("mensaje", "Error temporal de comunicación con el servicio de categorías.");
        }
        return new ResponseEntity<>(errorJson, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> erroresDeNegocio(RuntimeException ex) {
        Map<String, String> errorJson = new HashMap<>();
        errorJson.put("mensaje", ex.getMessage());
        return new ResponseEntity<>(errorJson, HttpStatus.BAD_REQUEST);
    }
}