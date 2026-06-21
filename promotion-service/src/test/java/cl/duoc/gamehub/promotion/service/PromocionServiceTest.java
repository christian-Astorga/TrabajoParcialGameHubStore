package cl.duoc.gamehub.promotion.service;

import cl.duoc.gamehub.promotion.dto.PromotionRequestDTO;
import cl.duoc.gamehub.promotion.dto.ValidateCouponDTO;
import cl.duoc.gamehub.promotion.model.Promocion;
import cl.duoc.gamehub.promotion.repository.PromocionRepository;
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
class PromocionServiceTest {

    @Mock
    private PromocionRepository promocionRepository;

    @InjectMocks
    private PromocionService promocionService;

    @Test
    void crearPromocion_DatosValidos_GuardaYRetorna() {
        PromotionRequestDTO request = new PromotionRequestDTO();
        request.setCodigo("SUMMER26");
        request.setTipo("FIJO");
        request.setValor(5000.0);
        request.setFechaInicio(LocalDate.now());
        request.setFechaFin(LocalDate.now().plusDays(10));
        request.setMontoMinimo(15000.0);
        request.setUsosMaximos(100);

        Promocion promoGuardada = new Promocion();
        promoGuardada.setCodigo("SUMMER26");
        promoGuardada.setEstado("ACTIVO");

        when(promocionRepository.findByCodigo("SUMMER26")).thenReturn(Optional.empty());
        when(promocionRepository.save(any(Promocion.class))).thenReturn(promoGuardada);

        Promocion resultado = promocionService.crearPromocion(request);

        assertNotNull(resultado);
        assertEquals("ACTIVO", resultado.getEstado());
        verify(promocionRepository, times(1)).save(any(Promocion.class));
    }

    @Test
    void crearPromocion_CodigoYaExiste_LanzaExcepcion() {
        PromotionRequestDTO request = new PromotionRequestDTO();
        request.setCodigo("DUPLICADO");

        when(promocionRepository.findByCodigo("DUPLICADO")).thenReturn(Optional.of(new Promocion()));

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> promocionService.crearPromocion(request));
        assertEquals("El codigo de cupon 'DUPLICADO' ya existe registrado.", excepcion.getMessage());
        verify(promocionRepository, never()).save(any());
    }

    @Test
    void crearPromocion_FechasInvalidas_LanzaExcepcion() {
        PromotionRequestDTO request = new PromotionRequestDTO();
        request.setCodigo("FECHAMALA");
        request.setFechaInicio(LocalDate.now().plusDays(5));
        request.setFechaFin(LocalDate.now());

        when(promocionRepository.findByCodigo("FECHAMALA")).thenReturn(Optional.empty());

        IllegalArgumentException excepcion = assertThrows(IllegalArgumentException.class, () -> promocionService.crearPromocion(request));
        assertEquals("La fecha de termino no puede ser anterior a la de inicio.", excepcion.getMessage());
        verify(promocionRepository, never()).save(any());
    }

    @Test
    void validarYAplicarCupon_CuponValido_AplicaYAumentaUsos() {
        ValidateCouponDTO validacion = new ValidateCouponDTO();
        validacion.setCodigo("PROMO10");
        validacion.setTotalOrden(20000.0);

        Promocion promo = new Promocion();
        promo.setCodigo("PROMO10");
        promo.setEstado("ACTIVO");
        promo.setFechaInicio(LocalDate.now().minusDays(1));
        promo.setFechaFin(LocalDate.now().plusDays(5));
        promo.setUsosActuales(5);
        promo.setUsosMaximos(10);
        promo.setMontoMinimo(10000.0);
        promo.setValor(2000.0);

        when(promocionRepository.findByCodigo("PROMO10")).thenReturn(Optional.of(promo));
        when(promocionRepository.save(any(Promocion.class))).thenReturn(promo);

        Promocion resultado = promocionService.validarYAplicarCupon(validacion);

        assertEquals(6, resultado.getUsosActuales());
        assertEquals("ACTIVO", resultado.getEstado());
        verify(promocionRepository, times(1)).save(promo);
    }

    @Test
    void validarYAplicarCupon_AlcanzaLimite_CambiaEstadoAAgotado() {
        ValidateCouponDTO validacion = new ValidateCouponDTO();
        validacion.setCodigo("ULTIMO");
        validacion.setTotalOrden(20000.0);

        Promocion promo = new Promocion();
        promo.setCodigo("ULTIMO");
        promo.setEstado("ACTIVO");
        promo.setFechaInicio(LocalDate.now());
        promo.setFechaFin(LocalDate.now().plusDays(5));
        promo.setUsosActuales(9);
        promo.setUsosMaximos(10);
        promo.setMontoMinimo(10000.0);
        promo.setValor(2000.0);

        when(promocionRepository.findByCodigo("ULTIMO")).thenReturn(Optional.of(promo));
        when(promocionRepository.save(any(Promocion.class))).thenReturn(promo);

        Promocion resultado = promocionService.validarYAplicarCupon(validacion);

        assertEquals(10, resultado.getUsosActuales());
        assertEquals("AGOTADO", resultado.getEstado());
        verify(promocionRepository, times(1)).save(promo);
    }

    @Test
    void validarYAplicarCupon_Inactivo_LanzaExcepcion() {
        ValidateCouponDTO validacion = new ValidateCouponDTO();
        validacion.setCodigo("OFF");

        Promocion promo = new Promocion();
        promo.setEstado("INACTIVO");

        when(promocionRepository.findByCodigo("OFF")).thenReturn(Optional.of(promo));

        assertThrows(IllegalArgumentException.class, () -> promocionService.validarYAplicarCupon(validacion));
    }

    @Test
    void validarYAplicarCupon_Vencido_LanzaExcepcion() {
        ValidateCouponDTO validacion = new ValidateCouponDTO();
        validacion.setCodigo("OLD");

        Promocion promo = new Promocion();
        promo.setEstado("ACTIVO");
        promo.setFechaInicio(LocalDate.now().minusDays(10));
        promo.setFechaFin(LocalDate.now().minusDays(2));

        when(promocionRepository.findByCodigo("OLD")).thenReturn(Optional.of(promo));

        assertThrows(IllegalArgumentException.class, () -> promocionService.validarYAplicarCupon(validacion));
    }

    @Test
    void validarYAplicarCupon_MontoMinimoNoAlcanzado_LanzaExcepcion() {
        ValidateCouponDTO validacion = new ValidateCouponDTO();
        validacion.setCodigo("CARO");
        validacion.setTotalOrden(5000.0);

        Promocion promo = new Promocion();
        promo.setEstado("ACTIVO");
        promo.setFechaInicio(LocalDate.now().minusDays(1));
        promo.setFechaFin(LocalDate.now().plusDays(5));
        promo.setUsosActuales(0);
        promo.setUsosMaximos(10);
        promo.setMontoMinimo(10000.0);

        when(promocionRepository.findByCodigo("CARO")).thenReturn(Optional.of(promo));

        assertThrows(IllegalArgumentException.class, () -> promocionService.validarYAplicarCupon(validacion));
    }

    @Test
    void validarYAplicarCupon_DescuentoSuperaTotal_LanzaExcepcion() {
        ValidateCouponDTO validacion = new ValidateCouponDTO();
        validacion.setCodigo("GRATIS");
        validacion.setTotalOrden(5000.0);

        Promocion promo = new Promocion();
        promo.setEstado("ACTIVO");
        promo.setFechaInicio(LocalDate.now().minusDays(1));
        promo.setFechaFin(LocalDate.now().plusDays(5));
        promo.setUsosActuales(0);
        promo.setUsosMaximos(10);
        promo.setMontoMinimo(1000.0);
        promo.setValor(6000.0);

        when(promocionRepository.findByCodigo("GRATIS")).thenReturn(Optional.of(promo));

        assertThrows(IllegalArgumentException.class, () -> promocionService.validarYAplicarCupon(validacion));
    }

    @Test
    void listarTodas_RetornaLista() {
        when(promocionRepository.findAll()).thenReturn(List.of(new Promocion(), new Promocion()));
        List<Promocion> resultado = promocionService.listarTodas();
        assertEquals(2, resultado.size());
    }

    @Test
    void listarPorEstado_RetornaLista() {
        when(promocionRepository.findByEstado("ACTIVO")).thenReturn(List.of(new Promocion()));
        List<Promocion> resultado = promocionService.listarPorEstado("activo");
        assertEquals(1, resultado.size());
    }

    @Test
    void buscarPorId_Existe_RetornaPromocion() {
        Promocion promo = new Promocion();
        promo.setId(1L);
        when(promocionRepository.findById(1L)).thenReturn(Optional.of(promo));
        assertEquals(1L, promocionService.buscarPorId(1L).getId());
    }

    @Test
    void buscarPorCodigo_Existe_RetornaPromocion() {
        Promocion promo = new Promocion();
        promo.setCodigo("SALE");
        when(promocionRepository.findByCodigo("SALE")).thenReturn(Optional.of(promo));
        assertEquals("SALE", promocionService.buscarPorCodigo("sale").getCodigo());
    }

    @Test
    void actualizarFechasYCondiciones_DatosValidos_ActualizaCorrectamente() {
        Promocion promo = new Promocion();
        promo.setId(1L);
        promo.setFechaInicio(LocalDate.now().minusDays(5));
        promo.setUsosActuales(2);

        when(promocionRepository.findById(1L)).thenReturn(Optional.of(promo));
        when(promocionRepository.save(any(Promocion.class))).thenReturn(promo);

        Promocion resultado = promocionService.actualizarFechasYCondiciones(1L, LocalDate.now().plusDays(10), 50);

        assertEquals(LocalDate.now().plusDays(10), resultado.getFechaFin());
        assertEquals(50, resultado.getUsosMaximos());
        verify(promocionRepository).save(promo);
    }

    @Test
    void actualizarFechasYCondiciones_ReactivarAgotado_CambiaEstado() {
        Promocion promo = new Promocion();
        promo.setId(1L);
        promo.setFechaInicio(LocalDate.now().minusDays(5));
        promo.setUsosActuales(10);
        promo.setUsosMaximos(10);
        promo.setEstado("AGOTADO");

        when(promocionRepository.findById(1L)).thenReturn(Optional.of(promo));
        when(promocionRepository.save(any(Promocion.class))).thenReturn(promo);

        Promocion resultado = promocionService.actualizarFechasYCondiciones(1L, null, 20);

        assertEquals("ACTIVO", resultado.getEstado());
        verify(promocionRepository).save(promo);
    }

    @Test
    void actualizarFechasYCondiciones_UsosMenoresAActuales_LanzaExcepcion() {
        Promocion promo = new Promocion();
        promo.setId(1L);
        promo.setUsosActuales(5);

        when(promocionRepository.findById(1L)).thenReturn(Optional.of(promo));

        assertThrows(IllegalArgumentException.class, () -> promocionService.actualizarFechasYCondiciones(1L, null, 2));
    }

    @Test
    void desactivarPromocion_Existe_CambiaAInactivo() {
        Promocion promo = new Promocion();
        promo.setId(1L);
        promo.setEstado("ACTIVO");

        when(promocionRepository.findById(1L)).thenReturn(Optional.of(promo));

        promocionService.desactivarPromocion(1L);

        assertEquals("INACTIVO", promo.getEstado());
        verify(promocionRepository).save(promo);
    }
}