package cl.duoc.gamehub.user.service;

import cl.duoc.gamehub.user.dto.UsuarioDTO;
import cl.duoc.gamehub.user.model.Usuario;
import cl.duoc.gamehub.user.repository.UsuarioRepository;
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
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void registrarUsuario_DatosValidos_GuardaYRetorna() {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setNombre("Test User");
        dto.setEmail("test@gamehub.cl");
        dto.setTelefono("123456789");
        dto.setRol("admin");

        Usuario usuarioGuardado = new Usuario();
        usuarioGuardado.setNombre("Test User");
        usuarioGuardado.setEmail("test@gamehub.cl");
        usuarioGuardado.setRol("ADMIN");
        usuarioGuardado.setEstado("ACTIVO");

        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioGuardado);

        Usuario resultado = usuarioService.registrarUsuario(dto);

        assertNotNull(resultado);
        assertEquals("ADMIN", resultado.getRol());
        assertEquals("ACTIVO", resultado.getEstado());
        verify(usuarioRepository, times(1)).save(any(Usuario.class));
    }

    @Test
    void listarTodos_RetornaLista() {
        when(usuarioRepository.findAll()).thenReturn(List.of(new Usuario(), new Usuario()));
        List<Usuario> resultado = usuarioService.listarTodos();
        assertEquals(2, resultado.size());
    }

    @Test
    void listarPorRol_RetornaLista() {
        when(usuarioRepository.findByRol("ADMIN")).thenReturn(List.of(new Usuario()));
        List<Usuario> resultado = usuarioService.listarPorRol("admin");
        assertEquals(1, resultado.size());
        verify(usuarioRepository, times(1)).findByRol("ADMIN");
    }

    @Test
    void listarPorEstado_RetornaLista() {
        when(usuarioRepository.findByEstado("ACTIVO")).thenReturn(List.of(new Usuario()));
        List<Usuario> resultado = usuarioService.listarPorEstado("activo");
        assertEquals(1, resultado.size());
        verify(usuarioRepository, times(1)).findByEstado("ACTIVO");
    }

    @Test
    void buscarPorId_UsuarioExiste_RetornaUsuario() {
        Usuario u = new Usuario();
        u.setId(1L);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(u));
        assertEquals(1L, usuarioService.buscarPorId(1L).getId());
    }

    @Test
    void buscarPorId_UsuarioNoExiste_LanzaExcepcion() {
        when(usuarioRepository.findById(1L)).thenReturn(Optional.empty());
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> usuarioService.buscarPorId(1L));
        assertEquals("Usuario no encontrado", excepcion.getMessage());
    }

    @Test
    void actualizarUsuario_UsuarioExiste_GuardaCambios() {
        UsuarioDTO dto = new UsuarioDTO();
        dto.setNombre("Nombre Editado");
        dto.setTelefono("987654321");
        dto.setEstado("inactivo");

        Usuario usuarioBD = new Usuario();
        usuarioBD.setId(1L);
        usuarioBD.setNombre("Nombre Original");
        usuarioBD.setEstado("ACTIVO");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioBD));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioBD);

        Usuario resultado = usuarioService.actualizarUsuario(1L, dto);

        assertEquals("Nombre Editado", resultado.getNombre());
        assertEquals("INACTIVO", resultado.getEstado());
        verify(usuarioRepository, times(1)).save(usuarioBD);
    }

    @Test
    void desactivarUsuario_UsuarioExiste_CambiaEstadoAInactivo() {
        Usuario usuarioBD = new Usuario();
        usuarioBD.setId(1L);
        usuarioBD.setEstado("ACTIVO");

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioBD));
        when(usuarioRepository.save(any(Usuario.class))).thenReturn(usuarioBD);

        Usuario resultado = usuarioService.desactivarUsuario(1L);

        assertEquals("INACTIVO", resultado.getEstado());
        verify(usuarioRepository, times(1)).save(usuarioBD);
    }
}