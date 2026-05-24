package cl.duoc.gamehub.product.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Captura el error de validación (400 Bad Request) según la tabla oficial de tu clase
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> manejarErroresValidacion(MethodArgumentNotValidException ex) {
        // Devuelve un mensaje simple y el estado 400 sin códigos raros ni avanzados
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Error de validación: Verifique que los campos obligatorios, precio y stock sean correctos.");
    }
}