package cl.duoc.gamehub.shipping.service;

import cl.duoc.gamehub.shipping.client.OrderClient;
import cl.duoc.gamehub.shipping.dto.OrderExternalDTO;
import cl.duoc.gamehub.shipping.dto.ShippingRequestDTO;
import cl.duoc.gamehub.shipping.model.Despacho;
import cl.duoc.gamehub.shipping.repository.DespachoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DespachoServiceTest {

    @Mock
    private DespachoRepository despachoRepository;

    @Mock
    private OrderClient orderClient;

    @InjectMocks
    private DespachoService despachoService;

    @Test
    void crearDespacho_OrdenPagada_GuardaYRetorna() {
        ShippingRequestDTO request = new ShippingRequestDTO();
        request.setOrdenId(1L);
        request.setUsuarioId(10L);
        request.setDireccion("Calle Falsa 123");
        request.setTransportista("Starken");

        OrderExternalDTO ordenExterna = new OrderExternalDTO();
        ordenExterna.setEstado("PAGADA");

        Despacho despachoGuardado = new Despacho();
        despachoGuardado.setOrdenId(1L);
        despachoGuardado.setEstado("PREPARACION");
        despachoGuardado.setTracking("TRK-12345678");

        when(orderClient.buscarOrdenPorId(1L)).thenReturn(ordenExterna);
        when(despachoRepository.save(any(Despacho.class))).thenReturn(despachoGuardado);

        Despacho resultado = despachoService.crearDespacho(request);

        assertNotNull(resultado);
        assertEquals("PREPARACION", resultado.getEstado());
        assertNotNull(resultado.getTracking());
        verify(despachoRepository, times(1)).save(any(Despacho.class));
    }

    @Test
    void crearDespacho_OrdenNoExiste_LanzaExcepcion() {
        ShippingRequestDTO request = new ShippingRequestDTO();
        request.setOrdenId(99L);

        when(orderClient.buscarOrdenPorId(99L)).thenThrow(new RuntimeException("Error Red"));

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> despachoService.crearDespacho(request));
        assertEquals("La orden indicada no existe en el sistema comercial.", excepcion.getMessage());
        verify(despachoRepository, never()).save(any());
    }

    @Test
    void crearDespacho_OrdenNoPagada_LanzaExcepcion() {
        ShippingRequestDTO request = new ShippingRequestDTO();
        request.setOrdenId(1L);

        OrderExternalDTO ordenExterna = new OrderExternalDTO();
        ordenExterna.setEstado("PENDIENTE");

        when(orderClient.buscarOrdenPorId(1L)).thenReturn(ordenExterna);

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> despachoService.crearDespacho(request));
        assertEquals("No se puede generar un despacho si la orden no se encuentra PAGADA.", excepcion.getMessage());
        verify(despachoRepository, never()).save(any());
    }

    @Test
    void listarTodos_RetornaLista() {
        when(despachoRepository.findAll()).thenReturn(List.of(new Despacho(), new Despacho()));
        List<Despacho> resultado = despachoService.listarTodos();
        assertEquals(2, resultado.size());
    }

    @Test
    void listarPorOrden_RetornaLista() {
        when(despachoRepository.findByOrdenId(1L)).thenReturn(List.of(new Despacho()));
        List<Despacho> resultado = despachoService.listarPorOrden(1L);
        assertEquals(1, resultado.size());
    }

    @Test
    void listarPorEstado_RetornaLista() {
        when(despachoRepository.findByEstado("PREPARACION")).thenReturn(List.of(new Despacho()));
        List<Despacho> resultado = despachoService.listarPorEstado("preparacion");
        assertEquals(1, resultado.size());
    }

    @Test
    void buscarPorId_Existe_RetornaDespacho() {
        Despacho despacho = new Despacho();
        despacho.setId(1L);
        when(despachoRepository.findById(1L)).thenReturn(Optional.of(despacho));
        assertEquals(1L, despachoService.buscarPorId(1L).getId());
    }

    @Test
    void buscarPorId_NoExiste_LanzaExcepcion() {
        when(despachoRepository.findById(1L)).thenReturn(Optional.empty());
        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> despachoService.buscarPorId(1L));
        assertEquals("No se encontro ningun registro de despacho con el ID: 1", excepcion.getMessage());
    }

    @Test
    void actualizarEstadoYTracking_DatosValidos_ActualizaCorrectamente() {
        Despacho despachoBD = new Despacho();
        despachoBD.setId(1L);
        despachoBD.setEstado("PREPARACION");

        when(despachoRepository.findById(1L)).thenReturn(Optional.of(despachoBD));
        when(despachoRepository.findByTracking("NUEVO-TRK")).thenReturn(Optional.empty());
        when(despachoRepository.save(any(Despacho.class))).thenReturn(despachoBD);

        Despacho resultado = despachoService.actualizarEstadoYTracking(1L, "EN CAMINO", "NUEVO-TRK");

        assertEquals("EN CAMINO", resultado.getEstado());
        assertEquals("NUEVO-TRK", resultado.getTracking());
        assertNull(resultado.getFechaEntrega());
        verify(despachoRepository, times(1)).save(despachoBD);
    }

    @Test
    void actualizarEstadoYTracking_Entregado_AsignaFechaEntrega() {
        Despacho despachoBD = new Despacho();
        despachoBD.setId(1L);
        despachoBD.setEstado("EN CAMINO");

        when(despachoRepository.findById(1L)).thenReturn(Optional.of(despachoBD));
        when(despachoRepository.save(any(Despacho.class))).thenReturn(despachoBD);

        Despacho resultado = despachoService.actualizarEstadoYTracking(1L, "ENTREGADO", null);

        assertEquals("ENTREGADO", resultado.getEstado());
        assertNotNull(resultado.getFechaEntrega());
        assertEquals(LocalDate.now(), resultado.getFechaEntrega());
        verify(despachoRepository, times(1)).save(despachoBD);
    }

    @Test
    void actualizarEstadoYTracking_TrackingOcupadoPorOtro_LanzaExcepcion() {
        Despacho despachoBD = new Despacho();
        despachoBD.setId(1L);

        Despacho despachoOcupado = new Despacho();
        despachoOcupado.setId(2L);

        when(despachoRepository.findById(1L)).thenReturn(Optional.of(despachoBD));
        when(despachoRepository.findByTracking("DUPLICADO")).thenReturn(Optional.of(despachoOcupado));

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> despachoService.actualizarEstadoYTracking(1L, "EN CAMINO", "DUPLICADO"));
        assertEquals("El numero de tracking ingresado ya esta asignado a otra entrega.", excepcion.getMessage());
        verify(despachoRepository, never()).save(any());
    }

    @Test
    void cancelarDespacho_DespachoNoEntregado_CambiaEstado() {
        Despacho despachoBD = new Despacho();
        despachoBD.setId(1L);
        despachoBD.setEstado("PREPARACION");

        when(despachoRepository.findById(1L)).thenReturn(Optional.of(despachoBD));

        despachoService.cancelarDespacho(1L);

        assertEquals("CANCELADO", despachoBD.getEstado());
        verify(despachoRepository, times(1)).save(despachoBD);
    }

    @Test
    void cancelarDespacho_DespachoEntregado_LanzaExcepcion() {
        Despacho despachoBD = new Despacho();
        despachoBD.setId(1L);
        despachoBD.setEstado("ENTREGADO");

        when(despachoRepository.findById(1L)).thenReturn(Optional.of(despachoBD));

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> despachoService.cancelarDespacho(1L));
        assertEquals("No se puede cancelar un envio que ya fue entregado conforme al cliente.", excepcion.getMessage());
        verify(despachoRepository, never()).save(any());
    }
}