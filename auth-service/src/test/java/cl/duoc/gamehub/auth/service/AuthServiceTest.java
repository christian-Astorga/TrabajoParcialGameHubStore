package cl.duoc.gamehub.auth.service;

import cl.duoc.gamehub.auth.dto.ActualizarCuentaDTO;
import cl.duoc.gamehub.auth.dto.RegistroRequestDTO;
import cl.duoc.gamehub.auth.model.CuentaAcceso;
import cl.duoc.gamehub.auth.repository.AuthRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthRepository authRepository;

    @InjectMocks
    private AuthService authService;

    @Test
    void autenticar_CredencialesCorrectas_RetornaToken() {
        String email = "admin@gamehub.cl";
        String password = "password123";

        CuentaAcceso cuenta = new CuentaAcceso();
        cuenta.setEmail(email);
        cuenta.setPasswordHash(password);
        cuenta.setEstado("ACTIVO");

        when(authRepository.findByEmail(email)).thenReturn(Optional.of(cuenta));

        String resultado = authService.autenticar(email, password);

        assertEquals("TOKEN_SIMULADO_GAMEHUB_SUCCESS", resultado);
        verify(authRepository, times(1)).findByEmail(email);
    }

    @Test
    void autenticar_ContrasenaIncorrecta_LanzaExcepcion() {
        String email = "admin@gamehub.cl";
        CuentaAcceso cuenta = new CuentaAcceso();
        cuenta.setEmail(email);
        cuenta.setPasswordHash("passwordReal");

        when(authRepository.findByEmail(email)).thenReturn(Optional.of(cuenta));

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> authService.autenticar(email, "mala"));
        assertEquals("Credenciales inválidas", excepcion.getMessage());
    }

    @Test
    void autenticar_CuentaInactiva_LanzaExcepcion() {
        String email = "baneado@gamehub.cl";
        CuentaAcceso cuenta = new CuentaAcceso();
        cuenta.setEmail(email);
        cuenta.setPasswordHash("1234");
        cuenta.setEstado("INACTIVO");

        when(authRepository.findByEmail(email)).thenReturn(Optional.of(cuenta));

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> authService.autenticar(email, "1234"));
        assertEquals("La cuenta se encuentra inactiva.", excepcion.getMessage());
    }

    @Test
    void crearCuenta_DatosNuevos_GuardaYRetornaCuenta() {
        RegistroRequestDTO dto = new RegistroRequestDTO();
        dto.setEmail("nuevo@gamehub.cl");
        dto.setPassword("12345");
        dto.setRol("USER");

        CuentaAcceso cuentaGuardada = new CuentaAcceso();
        cuentaGuardada.setEmail(dto.getEmail());
        cuentaGuardada.setRol(dto.getRol());
        cuentaGuardada.setEstado("ACTIVO");

        when(authRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
        when(authRepository.save(any(CuentaAcceso.class))).thenReturn(cuentaGuardada);

        CuentaAcceso resultado = authService.crearCuenta(dto);

        assertNotNull(resultado);
        assertEquals("nuevo@gamehub.cl", resultado.getEmail());
        assertEquals("ACTIVO", resultado.getEstado());
        verify(authRepository).save(any(CuentaAcceso.class));
    }

    @Test
    void crearCuenta_EmailYaExiste_LanzaExcepcion() {
        RegistroRequestDTO dto = new RegistroRequestDTO();
        dto.setEmail("existente@gamehub.cl");

        CuentaAcceso cuentaExistente = new CuentaAcceso();
        cuentaExistente.setEmail(dto.getEmail());

        when(authRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(cuentaExistente));

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> authService.crearCuenta(dto));
        assertEquals("El correo ya está registrado.", excepcion.getMessage());
        verify(authRepository, never()).save(any());
    }

    @Test
    void listarTodas_RetornaListaDeCuentas() {
        CuentaAcceso c1 = new CuentaAcceso();
        CuentaAcceso c2 = new CuentaAcceso();
        when(authRepository.findAll()).thenReturn(List.of(c1, c2));

        List<CuentaAcceso> resultado = authService.listarTodas();

        assertEquals(2, resultado.size());
        verify(authRepository, times(1)).findAll();
    }

    @Test
    void buscarPorId_CuentaExiste_RetornaCuenta() {
        CuentaAcceso cuenta = new CuentaAcceso();
        cuenta.setId(1L);
        when(authRepository.findById(1L)).thenReturn(Optional.of(cuenta));

        Optional<CuentaAcceso> resultado = authService.buscarPorId(1L);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
    }

    @Test
    void buscarPorCorreo_CuentaExiste_RetornaCuenta() {
        String email = "test@gamehub.cl";
        CuentaAcceso cuenta = new CuentaAcceso();
        cuenta.setEmail(email);
        when(authRepository.findByEmail(email)).thenReturn(Optional.of(cuenta));

        Optional<CuentaAcceso> resultado = authService.buscarPorCorreo(email);

        assertTrue(resultado.isPresent());
        assertEquals(email, resultado.get().getEmail());
    }

    @Test
    void actualizar_CuentaExisteYDatosValidos_GuardaCambios() {
        Long id = 1L;
        ActualizarCuentaDTO dto = new ActualizarCuentaDTO();
        dto.setPassword("nuevaPass");
        dto.setRol("ADMIN");
        dto.setEstado("INACTIVO");

        CuentaAcceso cuentaBD = new CuentaAcceso();
        cuentaBD.setId(id);
        cuentaBD.setPasswordHash("viejaPass");

        when(authRepository.findById(id)).thenReturn(Optional.of(cuentaBD));
        when(authRepository.save(any(CuentaAcceso.class))).thenReturn(cuentaBD);

        CuentaAcceso resultado = authService.actualizar(id, dto);

        assertEquals("nuevaPass", resultado.getPasswordHash());
        assertEquals("ADMIN", resultado.getRol());
        assertEquals("INACTIVO", resultado.getEstado());
        verify(authRepository).save(cuentaBD);
    }

    @Test
    void actualizar_CuentaNoExiste_LanzaExcepcion() {
        Long id = 99L;
        ActualizarCuentaDTO dto = new ActualizarCuentaDTO();

        when(authRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> authService.actualizar(id, dto));
        assertEquals("Cuenta no encontrada", excepcion.getMessage());
        verify(authRepository, never()).save(any());
    }

    @Test
    void desactivar_CuentaExiste_CambiaEstadoAInactivo() {
        Long id = 1L;
        CuentaAcceso cuentaBD = new CuentaAcceso();
        cuentaBD.setId(id);
        cuentaBD.setEstado("ACTIVO");

        when(authRepository.findById(id)).thenReturn(Optional.of(cuentaBD));
        when(authRepository.save(any(CuentaAcceso.class))).thenReturn(cuentaBD);

        CuentaAcceso resultado = authService.desactivar(id);

        assertEquals("INACTIVO", resultado.getEstado());
        verify(authRepository).save(cuentaBD);
    }

    @Test
    void desactivar_CuentaNoExiste_LanzaExcepcion() {
        Long id = 99L;

        when(authRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> authService.desactivar(id));
        assertEquals("Cuenta no encontrada", excepcion.getMessage());
        verify(authRepository, never()).save(any());
    }
}