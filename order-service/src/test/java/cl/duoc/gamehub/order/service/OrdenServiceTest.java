package cl.duoc.gamehub.order.service;

import cl.duoc.gamehub.order.client.InventarioClient;
import cl.duoc.gamehub.order.client.ProductoClient;
import cl.duoc.gamehub.order.dto.DetalleOrdenDTO;
import cl.duoc.gamehub.order.dto.OrdenDTO;
import cl.duoc.gamehub.order.model.DetalleOrden;
import cl.duoc.gamehub.order.model.Orden;
import cl.duoc.gamehub.order.repository.OrdenRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrdenServiceTest {

    @Mock
    private OrdenRepository ordenRepository;

    @Mock
    private ProductoClient productoClient;

    @Mock
    private InventarioClient inventarioClient;

    @InjectMocks
    private OrdenService ordenService;

    @Test
    void crearOrden_DatosValidos_CalculaTotalesYGuarda() {
        OrdenDTO dto = new OrdenDTO();
        dto.setUsuarioId(1L);

        DetalleOrdenDTO detalleDTO = new DetalleOrdenDTO();
        detalleDTO.setProductoId(10L);
        detalleDTO.setCantidad(2);

        List<DetalleOrdenDTO> detallesList = new ArrayList<>();
        detallesList.add(detalleDTO);
        dto.setDetalles(detallesList);

        Map<String, Object> productoJson = new HashMap<>();
        productoJson.put("precio", 15000.0);

        Orden ordenGuardada = new Orden();
        ordenGuardada.setId(100L);
        ordenGuardada.setSubtotal(30000.0);
        ordenGuardada.setTotal(30000.0);

        when(productoClient.buscarPorId(10L)).thenReturn(productoJson);
        when(ordenRepository.save(any(Orden.class))).thenReturn(ordenGuardada);

        Orden resultado = ordenService.crearOrden(dto);

        assertNotNull(resultado);
        verify(productoClient, times(1)).buscarPorId(10L);
        verify(inventarioClient, times(1)).reservarStockTemporal(anyMap());
        verify(ordenRepository, times(1)).save(any(Orden.class));
    }

    @Test
    void listarTodas_RetornaListaDeOrdenes() {
        when(ordenRepository.findAll()).thenReturn(List.of(new Orden(), new Orden()));

        List<Orden> resultado = ordenService.listarTodas();

        assertEquals(2, resultado.size());
        verify(ordenRepository, times(1)).findAll();
    }

    @Test
    void listarPorCliente_RetornaListaDeOrdenes() {
        when(ordenRepository.findByUsuarioId(1L)).thenReturn(List.of(new Orden()));

        List<Orden> resultado = ordenService.listarPorCliente(1L);

        assertEquals(1, resultado.size());
        verify(ordenRepository, times(1)).findByUsuarioId(1L);
    }

    @Test
    void listarPorEstado_RetornaListaDeOrdenes() {
        when(ordenRepository.findByEstado("PENDIENTE")).thenReturn(List.of(new Orden()));

        List<Orden> resultado = ordenService.listarPorEstado("pendiente");

        assertEquals(1, resultado.size());
        verify(ordenRepository, times(1)).findByEstado("PENDIENTE");
    }

    @Test
    void buscarPorId_OrdenExiste_RetornaOrden() {
        Orden orden = new Orden();
        orden.setId(1L);

        when(ordenRepository.findById(1L)).thenReturn(Optional.of(orden));

        Orden resultado = ordenService.buscarPorId(1L);

        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
    }

    @Test
    void buscarPorId_OrdenNoExiste_LanzaExcepcion() {
        when(ordenRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> ordenService.buscarPorId(1L));
        assertEquals("La orden solicitada no existe.", excepcion.getMessage());
    }

    @Test
    void actualizarEstado_EstadoPermitido_GuardaCambios() {
        Orden ordenBD = new Orden();
        ordenBD.setId(1L);
        ordenBD.setEstado("PENDIENTE");

        when(ordenRepository.findById(1L)).thenReturn(Optional.of(ordenBD));
        when(ordenRepository.save(any(Orden.class))).thenReturn(ordenBD);

        Orden resultado = ordenService.actualizarEstado(1L, "ENVIADA");

        assertEquals("ENVIADA", resultado.getEstado());
        verify(ordenRepository, times(1)).save(ordenBD);
    }

    @Test
    void actualizarEstado_OrdenPagada_LanzaExcepcion() {
        Orden ordenBD = new Orden();
        ordenBD.setId(1L);
        ordenBD.setEstado("PAGADA");

        when(ordenRepository.findById(1L)).thenReturn(Optional.of(ordenBD));

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> ordenService.actualizarEstado(1L, "ENVIADA"));
        assertTrue(excepcion.getMessage().contains("Regla de negocio: No se puede modificar el estado"));
        verify(ordenRepository, never()).save(any());
    }

    @Test
    void cancelarOrden_OrdenValida_LiberaStockYCambiaEstado() {
        Orden ordenBD = new Orden();
        ordenBD.setId(1L);
        ordenBD.setEstado("PENDIENTE");

        DetalleOrden detalle = new DetalleOrden();
        detalle.setProductoId(10L);
        detalle.setCantidad(2);

        List<DetalleOrden> detalles = new ArrayList<>();
        detalles.add(detalle);
        ordenBD.setDetalles(detalles);

        when(ordenRepository.findById(1L)).thenReturn(Optional.of(ordenBD));
        when(ordenRepository.save(any(Orden.class))).thenReturn(ordenBD);

        ordenService.cancelarOrden(1L);

        assertEquals("CANCELADA", ordenBD.getEstado());
        verify(inventarioClient, times(1)).actualizarCantidades(10L, 2);
        verify(ordenRepository, times(1)).save(ordenBD);
    }

    @Test
    void cancelarOrden_OrdenPagada_LanzaExcepcion() {
        Orden ordenBD = new Orden();
        ordenBD.setId(1L);
        ordenBD.setEstado("PAGADA");

        when(ordenRepository.findById(1L)).thenReturn(Optional.of(ordenBD));

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> ordenService.cancelarOrden(1L));
        assertEquals("No se puede cancelar una orden que ya ha sido pagada con éxito.", excepcion.getMessage());
        verify(inventarioClient, never()).actualizarCantidades(anyLong(), anyInt());
        verify(ordenRepository, never()).save(any());
    }

    @Test
    void cancelarOrden_OrdenYaCancelada_LanzaExcepcion() {
        Orden ordenBD = new Orden();
        ordenBD.setId(1L);
        ordenBD.setEstado("CANCELADA");

        when(ordenRepository.findById(1L)).thenReturn(Optional.of(ordenBD));

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> ordenService.cancelarOrden(1L));
        assertEquals("La orden ya se encuentra cancelada.", excepcion.getMessage());
        verify(inventarioClient, never()).actualizarCantidades(anyLong(), anyInt());
        verify(ordenRepository, never()).save(any());
    }
}