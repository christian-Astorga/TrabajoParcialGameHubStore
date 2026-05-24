package cl.duoc.gamehub.auth.controller;

import cl.duoc.gamehub.auth.dto.LoginRequestDTO;
import cl.duoc.gamehub.auth.dto.RegistroRequestDTO;
import cl.duoc.gamehub.auth.model.Usuario;
import cl.duoc.gamehub.auth.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UsuarioController { // Nombre corregido e idéntico al archivo .java

    @Autowired
    private UsuarioService usuarioService;

    // Registrar un nuevo usuario usando DTO
    @PostMapping("/registrar")
    public ResponseEntity<String> registrarCuenta(@Valid @RequestBody RegistroRequestDTO dto) {
        // Traspaso manual del DTO a la Entidad real de tu proyecto
        Usuario usuario = new Usuario();
        usuario.setUsername(dto.getUsername());
        usuario.setPassword(dto.getPassword());
        usuario.setNombre(dto.getNombre());

        String resultado = usuarioService.registrarUsuario(usuario);

        if (resultado.contains("Error")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(resultado);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }

    // Iniciar Sesión usando DTO
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody LoginRequestDTO dto) {
        String resultado = usuarioService.iniciarSesion(dto.getUsername(), dto.getPassword());

        if (resultado.contains("Error")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(resultado);
        }
        return ResponseEntity.status(HttpStatus.OK).body(resultado);
    }
}