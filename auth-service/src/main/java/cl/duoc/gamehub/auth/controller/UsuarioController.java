package cl.duoc.gamehub.auth.controller;

import cl.duoc.gamehub.auth.model.Usuario;
import cl.duoc.gamehub.auth.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @PostMapping("/register")
    public ResponseEntity<String> registrarUsuario(@RequestBody Usuario usuario) {
        String resultado = usuarioService.registrarUsuario(usuario);

        // Si el servicio responde con un error, mandamos un BadRequest (400)
        if (resultado.startsWith("Error")) {
            return new ResponseEntity<>(resultado, HttpStatus.BAD_REQUEST);
        }

        // Si todo sale bien, mandamos un Created (201) con el mensaje de éxito
        return new ResponseEntity<>(resultado, HttpStatus.CREATED);
    }
}