package cl.duoc.gamehub.payment.service;

import cl.duoc.gamehub.payment.client.OrderClient;
import cl.duoc.gamehub.payment.dto.PaymentRequestDTO;
import cl.duoc.gamehub.payment.model.Pago;
import cl.duoc.gamehub.payment.repository.PagoRepository;
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
class PagoServiceTest {

    @Mock
    private PagoRepository pagoRepository;

    @Mock
    private OrderClient orderClient;

    @InjectMocks
    private PagoService pagoService;

    @Test
    void procesarPago_PagoExitoso_GuardaYNotificaOrderService() {
        PaymentRequestDTO dto = new PaymentRequestDTO();
        dto.setOrdenId(1L);
        dto.setMonto(15000.0);
        dto.setMetodo("CREDITO");

        Pago pagoGuardado = new Pago();
        pagoGuardado.setOrdenId(1L);
        pagoGuardado.setMonto(15000.0);
        pagoGuardado.setMetodoPago("CREDITO");
        pagoGuardado.setEstado("APROBADO");
        pagoGuardado.setTransaccionId("TRX-12345678");

        when(pagoRepository.save(any(Pago.class))).thenReturn(pagoGuardado);

        Pago resultado = pagoService.procesarPago(dto);

        assertNotNull(resultado);
        assertEquals("APROBADO", resultado.getEstado());
        assertNotNull(resultado.getTransaccionId());
        verify(pagoRepository, times(1)).save(any(Pago.class));
        verify(orderClient, times(1)).actualizarEstado(1L, "PAGADA");
    }

    @Test
    void procesarPago_FallaComunicacionConOrderService_LanzaExcepcion() {
        PaymentRequestDTO dto = new PaymentRequestDTO();
        dto.setOrdenId(1L);
        dto.setMonto(15000.0);
        dto.setMetodo("DEBITO");

        Pago pagoGuardado = new Pago();
        pagoGuardado.setOrdenId(1L);
        pagoGuardado.setEstado("APROBADO");

        when(pagoRepository.save(any(Pago.class))).thenReturn(pagoGuardado);
        doThrow(new RuntimeException("Timeout de red")).when(orderClient).actualizarEstado(anyLong(), anyString());

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> pagoService.procesarPago(dto));
        assertEquals("El pago fue procesado pero no se pudo actualizar el estado de la orden remota. Consistencia bajo revisión.", excepcion.getMessage());
        verify(pagoRepository, times(1)).save(any(Pago.class));
    }

    @Test
    void listarTodos_RetornaListaDePagos() {
        when(pagoRepository.findAll()).thenReturn(List.of(new Pago(), new Pago()));

        List<Pago> resultado = pagoService.listarTodos();

        assertEquals(2, resultado.size());
        verify(pagoRepository, times(1)).findAll();
    }

    @Test
    void buscarPorId_PagoExiste_RetornaPago() {
        Pago pago = new Pago();
        pago.setId(10L);

        when(pagoRepository.findById(10L)).thenReturn(Optional.of(pago));

        Pago resultado = pagoService.buscarPorId(10L);

        assertNotNull(resultado);
        assertEquals(10L, resultado.getId());
    }

    @Test
    void buscarPorId_PagoNoExiste_LanzaExcepcion() {
        when(pagoRepository.findById(10L)).thenReturn(Optional.empty());

        RuntimeException excepcion = assertThrows(RuntimeException.class, () -> pagoService.buscarPorId(10L));
        assertEquals("Registro de pago no encontrado.", excepcion.getMessage());
    }

    @Test
    void buscarPorOrdenId_RetornaListaDePagos() {
        when(pagoRepository.findByOrdenId(1L)).thenReturn(List.of(new Pago()));

        List<Pago> resultado = pagoService.buscarPorOrdenId(1L);

        assertEquals(1, resultado.size());
        verify(pagoRepository, times(1)).findByOrdenId(1L);
    }
}