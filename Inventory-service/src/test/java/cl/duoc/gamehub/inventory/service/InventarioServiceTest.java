package cl.duoc.gamehub.inventory.service;

import cl.duoc.gamehub.inventory.client.ProductoClient;
import cl.duoc.gamehub.inventory.dto.InventarioDTO;
import cl.duoc.gamehub.inventory.dto.ReservaDTO;
import cl.duoc.gamehub.inventory.model.Inventario;
import cl.duoc.gamehub.inventory.model.MovimientoInventario;
import cl.duoc.gamehub.inventory.repository.InventarioRepository;
import cl.duoc.gamehub.inventory.repository.MovimientoInventarioRepository;
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
class InventarioServiceTest {

    @Mock
    private InventarioRepository inventarioRepository;

    @Mock
    private MovimientoInventarioRepository movimientoRepository;

    @Mock
    private ProductoClient productoClient;

    @InjectMocks
    private InventarioService inventarioService;

    @Test
    void crearRegistroStock_DatosNuevos_GuardaInventarioYMovimiento() {
        InventarioDTO dto = new InventarioDTO();
        dto.setProductoId(1L);
        dto.setStockDisponible(50);
        dto.setStockMinimo(5);
        dto.setUbicacion("bodega 1");

        Inventario inventarioGuardado = new Inventario();
        inventarioGuardado.setProductoId(1L);
        inventarioGuardado.setStockDisponible(50);
        inventarioGuardado.setStockReservado(0);
        inventarioGuardado.setStockMinimo(5);
        inventarioGuardado.setUbicacion("BODEGA 1");

        when(inventarioRepository.findByProductoId(1L)).thenReturn(Optional.empty());
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(inventarioGuardado);

        Inventario resultado = inventarioService.crearRegistroStock(dto);

        assertNotNull(resultado);
        assertEquals(50, resultado.getStockDisponible());
        assertEquals("BODEGA 1", resultado.getUbicacion());
        verify(productoClient, times(1)).buscarPorId(1L);
        verify(inventarioRepository, times(1)).save(any(Inventario.class));
        verify(movimientoRepository, times(1)).save(any(MovimientoInventario.class));
    }

    @Test
    void crearRegistroStock_InventarioYaExiste_LanzaExcepcion() {
        InventarioDTO dto = new InventarioDTO();
        dto.setProductoId(1L);

        when(inventarioRepository.findByProductoId(1L)).thenReturn(Optional.of(new Inventario()));

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> inventarioService.crearRegistroStock(dto));
        assertEquals("El producto ya cuenta con un registro de inventario activo. Use actualización de cantidades.", excepcion.getMessage());
        verify(inventarioRepository, never()).save(any());
        verify(movimientoRepository, never()).save(any());
    }

    @Test
    void listarTodo_RetornaListaDeInventario() {
        when(inventarioRepository.findAll()).thenReturn(List.of(new Inventario(), new Inventario()));

        List<Inventario> resultado = inventarioService.listarTodo();

        assertEquals(2, resultado.size());
        verify(inventarioRepository, times(1)).findAll();
    }

    @Test
    void buscarPorId_InventarioExiste_RetornaInventario() {
        Inventario inv = new Inventario();
        inv.setId(10L);

        when(inventarioRepository.findById(10L)).thenReturn(Optional.of(inv));

        Inventario resultado = inventarioService.buscarPorId(10L);

        assertNotNull(resultado);
        assertEquals(10L, resultado.getId());
    }

    @Test
    void buscarPorId_InventarioNoExiste_LanzaExcepcion() {
        when(inventarioRepository.findById(10L)).thenReturn(Optional.empty());

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> inventarioService.buscarPorId(10L));
        assertEquals("Registro de inventario no encontrado.", excepcion.getMessage());
    }

    @Test
    void buscarPorProductoId_InventarioExiste_RetornaInventario() {
        Inventario inv = new Inventario();
        inv.setProductoId(1L);

        when(inventarioRepository.findByProductoId(1L)).thenReturn(Optional.of(inv));

        Inventario resultado = inventarioService.buscarPorProductoId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getProductoId());
    }

    @Test
    void buscarPorProductoId_InventarioNoExiste_LanzaExcepcion() {
        when(inventarioRepository.findByProductoId(1L)).thenReturn(Optional.empty());

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> inventarioService.buscarPorProductoId(1L));
        assertEquals("No existe registro de inventario para el producto ID especificado.", excepcion.getMessage());
    }

    @Test
    void reservarStockTemporal_StockSuficiente_AjustaCantidadesYGuarda() {
        ReservaDTO dto = new ReservaDTO();
        dto.setProductoId(1L);
        dto.setCantidad(5);

        Inventario invActual = new Inventario();
        invActual.setProductoId(1L);
        invActual.setStockDisponible(20);
        invActual.setStockReservado(2);

        when(inventarioRepository.findByProductoId(1L)).thenReturn(Optional.of(invActual));
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(invActual);

        Inventario resultado = inventarioService.reservarStockTemporal(dto);

        assertEquals(15, resultado.getStockDisponible());
        assertEquals(7, resultado.getStockReservado());
        verify(inventarioRepository, times(1)).save(invActual);
        verify(movimientoRepository, times(1)).save(any(MovimientoInventario.class));
    }

    @Test
    void reservarStockTemporal_StockInsuficiente_LanzaExcepcion() {
        ReservaDTO dto = new ReservaDTO();
        dto.setProductoId(1L);
        dto.setCantidad(50);

        Inventario invActual = new Inventario();
        invActual.setProductoId(1L);
        invActual.setStockDisponible(20);

        when(inventarioRepository.findByProductoId(1L)).thenReturn(Optional.of(invActual));

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> inventarioService.reservarStockTemporal(dto));
        assertEquals("No se pueden reservar más unidades que el stock disponible actualmente.", excepcion.getMessage());
        verify(inventarioRepository, never()).save(any());
    }

    @Test
    void actualizarCantidades_IncrementoPositivo_RegistraEntrada() {
        Long productoId = 1L;
        Inventario invActual = new Inventario();
        invActual.setProductoId(productoId);
        invActual.setStockDisponible(10);

        when(inventarioRepository.findByProductoId(productoId)).thenReturn(Optional.of(invActual));
        when(inventarioRepository.save(any(Inventario.class))).thenReturn(invActual);

        Inventario resultado = inventarioService.actualizarCantidades(productoId, 15);

        assertEquals(15, resultado.getStockDisponible());
        verify(inventarioRepository).save(invActual);
        verify(movimientoRepository).save(any(MovimientoInventario.class));
    }

    @Test
    void actualizarCantidades_StockNegativo_LanzaExcepcion() {
        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> inventarioService.actualizarCantidades(1L, -5));
        assertEquals("El stock disponible no puede quedar negativo.", excepcion.getMessage());
        verify(inventarioRepository, never()).save(any());
    }

    @Test
    void eliminarRegistroStockObsoleto_RegistroExiste_EliminaYGuardaMovimientoSalida() {
        Inventario inv = new Inventario();
        inv.setId(10L);
        inv.setProductoId(1L);
        inv.setStockDisponible(5);

        when(inventarioRepository.findById(10L)).thenReturn(Optional.of(inv));

        inventarioService.eliminarRegistroStockObsoleto(10L);

        verify(movimientoRepository, times(1)).save(any(MovimientoInventario.class));
        verify(inventarioRepository, times(1)).delete(inv);
    }
}