package cl.duoc.gamehub.category.service;

import cl.duoc.gamehub.category.dto.CategoriaDTO;
import cl.duoc.gamehub.category.model.Categoria;
import cl.duoc.gamehub.category.repository.CategoriaRepository;
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
class CategoriaServiceTest {

    @Mock
    private CategoriaRepository categoriaRepository;

    @InjectMocks
    private CategoriaService categoriaService;

    @Test
    void crearCategoria_DatosNuevos_GuardaYRetornaCategoria() {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setNombre(" rpg ");
        dto.setDescripcion("Juegos de rol");

        Categoria categoriaGuardada = new Categoria();
        categoriaGuardada.setNombre("RPG");
        categoriaGuardada.setDescripcion("Juegos de rol");
        categoriaGuardada.setEstado("ACTIVO");

        when(categoriaRepository.findByNombre("RPG")).thenReturn(Optional.empty());
        when(categoriaRepository.save(any(Categoria.class))).thenReturn(categoriaGuardada);

        Categoria resultado = categoriaService.crearCategoria(dto);

        assertNotNull(resultado);
        assertEquals("RPG", resultado.getNombre());
        assertEquals("ACTIVO", resultado.getEstado());
        verify(categoriaRepository).save(any(Categoria.class));
    }

    @Test
    void crearCategoria_NombreYaExiste_LanzaExcepcion() {
        CategoriaDTO dto = new CategoriaDTO();
        dto.setNombre("SHOOTER");

        Categoria categoriaExistente = new Categoria();
        categoriaExistente.setNombre("SHOOTER");

        when(categoriaRepository.findByNombre("SHOOTER")).thenReturn(Optional.of(categoriaExistente));

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> categoriaService.crearCategoria(dto));
        assertEquals("El nombre de la categoría ya existe.", excepcion.getMessage());
        verify(categoriaRepository, never()).save(any());
    }

    @Test
    void listarTodas_RetornaListaDeCategorias() {
        when(categoriaRepository.findAll()).thenReturn(List.of(new Categoria(), new Categoria()));

        List<Categoria> resultado = categoriaService.listarTodas();

        assertEquals(2, resultado.size());
        verify(categoriaRepository, times(1)).findAll();
    }

    @Test
    void buscarPorId_CategoriaExiste_RetornaCategoria() {
        Categoria categoria = new Categoria();
        categoria.setId(1L);
        categoria.setNombre("ESTRATEGIA");

        when(categoriaRepository.findById(1L)).thenReturn(Optional.of(categoria));

        Categoria resultado = categoriaService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void buscarPorId_CategoriaNoExiste_LanzaExcepcion() {
        when(categoriaRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> categoriaService.buscarPorId(99L));
        assertEquals("Categoría no encontrada.", excepcion.getMessage());
    }
    }