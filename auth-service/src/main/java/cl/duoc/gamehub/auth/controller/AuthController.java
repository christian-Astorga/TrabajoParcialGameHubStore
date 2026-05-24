package cl.duoc.gamehub.auth.controller;

import cl.duoc.gamehub.auth.dto.LoginRequestDTO;
import cl.duoc.gamehub.auth.dto.RegistroRequestDTO;
import cl.duoc.gamehub.auth.model.Auth;
import cl.duoc.gamehub.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController { // Nombre corregido e idéntico al archivo .java

    @Autowired
    private AuthService authService;

    // Registrar un nuevo usuario usando DTO
    @PostMapping("/registrar")
    public ResponseEntity<String> registrarCuenta(@Valid @RequestBody RegistroRequestDTO dto) {
        // Traspaso manual del DTO a la Entidad real de tu proyecto
        Auth auth = new Auth();
        auth.setUsername(dto.getUsername());
        auth.setPassword(dto.getPassword());
        auth.setNombre(dto.getNombre());

        String resultado = authService.registrarUsuario(auth);

        if (resultado.contains("Error")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultado);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }

    // Iniciar Sesión usando DTO
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequestDTO dto) {
        String resultado = authService.iniciarSesion(dto.getUsername(), dto.getPassword());

        if (resultado.contains("Error")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resultado);
        }
        return ResponseEntity.status(HttpStatus.OK).body(resultado);
    }
}