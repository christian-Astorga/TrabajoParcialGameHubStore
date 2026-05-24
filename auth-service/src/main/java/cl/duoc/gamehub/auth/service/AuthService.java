package cl.duoc.gamehub.auth.service;

import cl.duoc.gamehub.auth.model.CuentaAcceso;
import cl.duoc.gamehub.auth.repository.AuthRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private AuthRepository authRepository;

    public String autenticar(String email, String password) {
        Optional<CuentaAcceso> usuarioOpt = authRepository.findByEmail(email);

        if (usuarioOpt.isEmpty()) {
            throw new RuntimeException("Credenciales inválidas: El correo no existe.");
        }

        CuentaAcceso usuario = usuarioOpt.get();
        if ("INACTIVO".equals(usuario.getEstado())) {
            throw new RuntimeException("Acceso denegado: El usuario está inactivo.");
        }

        // Simulación de contraseña exitosa (en un entorno real usarías BCrypt)
        return "Login exitoso. Token generado para el usuario con Rol: " + usuario.getRol();
    }
}