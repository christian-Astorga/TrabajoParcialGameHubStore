package cl.duoc.gamehub.review.service;

import cl.duoc.gamehub.review.client.OrderClient;
import cl.duoc.gamehub.review.dto.OrderValidationDTO;
import cl.duoc.gamehub.review.dto.ReviewRequestDTO;
import cl.duoc.gamehub.review.model.Reseña;
import cl.duoc.gamehub.review.repository.ReseñaRepository;
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
class ReseñaServiceTest {

    @Mock
    private ReseñaRepository resenaRepository;

    @Mock
    private OrderClient orderClient;

    @InjectMocks
    private ReseñaService resenaService;

    @Test
    void crearResena_DatosValidos_GuardaYRetorna() {
        ReviewRequestDTO request = new ReviewRequestDTO();
        request.setUsuarioId(1L);
        request.setProductoId(10L);
        request.setOrdenId(100L);
        request.setPuntuacion(5);
        request.setComentario("Excelente juego");

        OrderValidationDTO ordenValidacion = new OrderValidationDTO();
        ordenValidacion.setUsuarioId(1L);
        ordenValidacion.setEstado("PAGADA");

        Reseña resenaGuardada = new Reseña();
        resenaGuardada.setUsuarioId(1L);
        resenaGuardada.setProductoId(10L);
        resenaGuardada.setEstado("APROBADO");

        when(resenaRepository.findByUsuarioIdAndProductoIdAndOrdenId(1L, 10L, 100L)).thenReturn(Optional.empty());
        when(orderClient.buscarOrdenPorId(100L)).thenReturn(ordenValidacion);
        when(resenaRepository.save(any(Reseña.class))).thenReturn(resenaGuardada);

        Reseña resultado = resenaService.crearResena(request);

        assertNotNull(resultado);
        assertEquals("APROBADO", resultado.getEstado());
        verify(resenaRepository, times(1)).save(any(Reseña.class));
    }

    @Test
    void crearResena_ResenaYaExiste_LanzaExcepcion() {
        ReviewRequestDTO request = new ReviewRequestDTO();
        request.setUsuarioId(1L);
        request.setProductoId(10L);
        request.setOrdenId(100L);

        when(resenaRepository.findByUsuarioIdAndProductoIdAndOrdenId(1L, 10L, 100L)).thenReturn(Optional.of(new Reseña()));

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> resenaService.crearResena(request));
        assertEquals("Ya has registrado una reseña para este producto en esta misma orden de compra.", excepcion.getMessage());
        verify(resenaRepository, never()).save(any());
    }

    @Test
    void crearResena_PuntuacionFueraDeRango_LanzaExcepcion() {
        ReviewRequestDTO request = new ReviewRequestDTO();
        request.setUsuarioId(1L);
        request.setProductoId(10L);
        request.setOrdenId(100L);
        request.setPuntuacion(6);

        when(resenaRepository.findByUsuarioIdAndProductoIdAndOrdenId(1L, 10L, 100L)).thenReturn(Optional.empty());

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> resenaService.crearResena(request));
        assertEquals("La calificacion debe ser un valor entero entre 1 y 5 estrellas.", excepcion.getMessage());
        verify(resenaRepository, never()).save(any());
    }

    @Test
    void crearResena_FallaComunicacionFeign_ContinuaPorFallback() {
        ReviewRequestDTO request = new ReviewRequestDTO();
        request.setUsuarioId(1L);
        request.setProductoId(10L);
        request.setOrdenId(100L);
        request.setPuntuacion(4);

        Reseña resenaGuardada = new Reseña();
        resenaGuardada.setEstado("APROBADO");

        when(resenaRepository.findByUsuarioIdAndProductoIdAndOrdenId(1L, 10L, 100L)).thenReturn(Optional.empty());
        when(orderClient.buscarOrdenPorId(100L)).thenThrow(new RuntimeException("Timeout"));
        when(resenaRepository.save(any(Reseña.class))).thenReturn(resenaGuardada);

        Reseña resultado = resenaService.crearResena(request);

        assertNotNull(resultado);
        verify(resenaRepository, times(1)).save(any(Reseña.class));
    }

    @Test
    void listarPorProducto_RetornaLista() {
        when(resenaRepository.findByProductoId(10L)).thenReturn(List.of(new Reseña()));
        List<Reseña> resultado = resenaService.listarPorProducto(10L);
        assertEquals(1, resultado.size());
    }

    @Test
    void listarPorUsuario_RetornaLista() {
        when(resenaRepository.findByUsuarioId(1L)).thenReturn(List.of(new Reseña(), new Reseña()));
        List<Reseña> resultado = resenaService.listarPorUsuario(1L);
        assertEquals(2, resultado.size());
    }

    @Test
    void buscarPorId_Existe_RetornaResena() {
        Reseña resena = new Reseña();
        resena.setId(1L);
        when(resenaRepository.findById(1L)).thenReturn(Optional.of(resena));
        assertEquals(1L, resenaService.buscarPorId(1L).getId());
    }

    @Test
    void buscarPorId_NoExiste_LanzaExcepcion() {
        when(resenaRepository.findById(1L)).thenReturn(Optional.empty());
        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> resenaService.buscarPorId(1L));
        assertEquals("No se encontro ninguna reseña con el ID: 1", excepcion.getMessage());
    }

    @Test
    void actualizarComentario_DatosValidos_GuardaCambios() {
        Reseña resenaBD = new Reseña();
        resenaBD.setId(1L);
        resenaBD.setPuntuacion(3);
        resenaBD.setComentario("Maso");

        when(resenaRepository.findById(1L)).thenReturn(Optional.of(resenaBD));
        when(resenaRepository.save(any(Reseña.class))).thenReturn(resenaBD);

        Reseña resultado = resenaService.actualizarComentario(1L, 5, "Mejoro con el parche");

        assertEquals(5, resultado.getPuntuacion());
        assertEquals("Mejoro con el parche", resultado.getComentario());
        verify(resenaRepository, times(1)).save(resenaBD);
    }

    @Test
    void actualizarComentario_PuntuacionInvalida_LanzaExcepcion() {
        Reseña resenaBD = new Reseña();
        resenaBD.setId(1L);

        when(resenaRepository.findById(1L)).thenReturn(Optional.of(resenaBD));

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> resenaService.actualizarComentario(1L, 0, "Malo"));
        assertEquals("La calificacion debe ser entre 1 y 5 estrellas.", excepcion.getMessage());
        verify(resenaRepository, never()).save(any());
    }

    @Test
    void moderarOEliminarResena_Existe_CambiaEstadoAModerado() {
        Reseña resenaBD = new Reseña();
        resenaBD.setId(1L);
        resenaBD.setEstado("APROBADO");

        when(resenaRepository.findById(1L)).thenReturn(Optional.of(resenaBD));
        when(resenaRepository.save(any(Reseña.class))).thenReturn(resenaBD);

        resenaService.moderarOEliminarResena(1L);

        assertEquals("MODERADO", resenaBD.getEstado());
        verify(resenaRepository, times(1)).save(resenaBD);
    }
}