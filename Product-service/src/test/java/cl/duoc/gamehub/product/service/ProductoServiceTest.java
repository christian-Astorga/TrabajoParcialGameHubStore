package cl.duoc.gamehub.product.service;

import cl.duoc.gamehub.product.client.CategoriaClient;
import cl.duoc.gamehub.product.model.Producto;
import cl.duoc.gamehub.product.repository.ProductoRepository;
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
class ProductoServiceTest {

    @Mock
    private ProductoRepository repository;

    @Mock
    private CategoriaClient categoriaClient;

    @InjectMocks
    private ProductoService productoService;

    @Test
    void crear_CategoriaExiste_GuardaYRetornaProducto() {
        Producto producto = new Producto();
        producto.setNombre("Juego Nuevo");
        producto.setCategoriaId(1L);

        Producto productoGuardado = new Producto();
        productoGuardado.setNombre("Juego Nuevo");
        productoGuardado.setCategoriaId(1L);
        productoGuardado.setEstado("ACTIVO");

        when(repository.save(any(Producto.class))).thenReturn(productoGuardado);

        Producto resultado = productoService.crear(producto);

        assertNotNull(resultado);
        assertEquals("ACTIVO", resultado.getEstado());
        verify(categoriaClient, times(1)).buscarPorId(1L);
        verify(repository, times(1)).save(producto);
    }

    @Test
    void crear_CategoriaNoExiste_LanzaExcepcion() {
        Producto producto = new Producto();
        producto.setCategoriaId(99L);

        doThrow(new RuntimeException("Not Found")).when(categoriaClient).buscarPorId(99L);

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> productoService.crear(producto));
        assertEquals("No se puede crear el producto: La categoría especificada no existe.", excepcion.getMessage());
        verify(repository, never()).save(any());
    }

    @Test
    void listarTodos_RetornaListaDeProductos() {
        when(repository.findAll()).thenReturn(List.of(new Producto(), new Producto()));

        List<Producto> resultado = productoService.listarTodos();

        assertEquals(2, resultado.size());
        verify(repository, times(1)).findAll();
    }

    @Test
    void buscarPorId_ProductoExiste_RetornaProducto() {
        Producto producto = new Producto();
        producto.setId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(producto));

        Producto resultado = productoService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void buscarPorId_ProductoNoExiste_LanzaExcepcion() {
        when(repository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> productoService.buscarPorId(1L));
        assertEquals("Producto no encontrado con ID: 1", excepcion.getMessage());
    }

    @Test
    void listarPorCategoria_RetornaLista() {
        when(repository.findByCategoriaId(1L)).thenReturn(List.of(new Producto()));

        List<Producto> resultado = productoService.listarPorCategoria(1L);

        assertEquals(1, resultado.size());
        verify(repository, times(1)).findByCategoriaId(1L);
    }

    @Test
    void actualizar_ProductoExiste_GuardaCambios() {
        Long id = 1L;
        Producto datos = new Producto();
        datos.setNombre("Juego Actualizado");
        datos.setDescripcion("Nueva descripcion");
        datos.setPrecio(20000.0);
        datos.setEstado("INACTIVO");

        Producto productoBD = new Producto();
        productoBD.setId(id);

        when(repository.findById(id)).thenReturn(Optional.of(productoBD));
        when(repository.save(any(Producto.class))).thenReturn(productoBD);

        Producto resultado = productoService.actualizar(id, datos);

        assertEquals("Juego Actualizado", resultado.getNombre());
        assertEquals("Nueva descripcion", resultado.getDescripcion());
        assertEquals(20000.0, resultado.getPrecio());
        assertEquals("INACTIVO", resultado.getEstado());
        verify(repository, times(1)).save(productoBD);
    }

    @Test
    void desactivar_ProductoExiste_CambiaEstadoAInactivo() {
        Long id = 1L;
        Producto productoBD = new Producto();
        productoBD.setId(id);
        productoBD.setEstado("ACTIVO");

        when(repository.findById(id)).thenReturn(Optional.of(productoBD));
        when(repository.save(any(Producto.class))).thenReturn(productoBD);

        Producto resultado = productoService.desactivar(id);

        assertEquals("INACTIVO", resultado.getEstado());
        verify(repository, times(1)).save(productoBD);
    }
}