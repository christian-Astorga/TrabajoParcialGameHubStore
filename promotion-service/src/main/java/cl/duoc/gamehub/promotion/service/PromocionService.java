package cl.duoc.gamehub.promotion.service;

import cl.duoc.gamehub.promotion.dto.PromotionRequestDTO;
import cl.duoc.gamehub.promotion.dto.ValidateCouponDTO;
import cl.duoc.gamehub.promotion.model.Promocion;
import cl.duoc.gamehub.promotion.repository.PromocionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class PromocionService {

    private static final Logger log = LoggerFactory.getLogger(PromocionService.class);
    private final PromocionRepository promocionRepository;

    public PromocionService(PromocionRepository promocionRepository) {
        this.promocionRepository = promocionRepository;
    }

    @Transactional
    public Promocion crearPromocion(PromotionRequestDTO request) {
        log.info("[AUDITORIA] Registrando nueva promocion con codigo: {}", request.getCodigo());

        promocionRepository.findByCodigo(request.getCodigo().toUpperCase()).ifPresent(p -> {
            throw new IllegalArgumentException("El codigo de cupon '" + request.getCodigo() + "' ya existe registrado.");
        });

        if (request.getFechaFin().isBefore(request.getFechaInicio())) {
            throw new IllegalArgumentException("La fecha de termino no puede ser anterior a la de inicio.");
        }

        Promocion promo = new Promocion();
        promo.setCodigo(request.getCodigo().toUpperCase());
        promo.setTipo(request.getTipo().toUpperCase());
        promo.setValor(request.getValor());
        promo.setFechaInicio(request.getFechaInicio());
        promo.setFechaFin(request.getFechaFin());
        promo.setMontoMinimo(request.getMontoMinimo());
        promo.setUsosMaximos(request.getUsosMaximos());
        promo.setUsosActuales(0);
        promo.setEstado("ACTIVO");

        return promocionRepository.save(promo);
    }

    @Transactional
    public Promocion validarYAplicarCupon(ValidateCouponDTO validacion) {
        log.info("[VALIDACION] Procesando cupon: {} para una orden de total: ${}", validacion.getCodigo(), validacion.getTotalOrden());

        Promocion promo = promocionRepository.findByCodigo(validacion.getCodigo().toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("El cupon ingresado no existe en el sistema."));

        if (!"ACTIVO".equalsIgnoreCase(promo.getEstado())) {
            throw new IllegalArgumentException("El cupon ingresado se encuentra inactivo.");
        }

        LocalDate hoy = LocalDate.now();
        if (hoy.isBefore(promo.getFechaInicio()) || hoy.isAfter(promo.getFechaFin())) {
            log.warn("[VALIDACION FALLIDA] Intento de usar cupon vencido o fuera de rango de vigencia.");
            throw new IllegalArgumentException("El cupon ingresado ya no se encuentra vigente (Vencido).");
        }

        if (promo.getUsosActuales() >= promo.getUsosMaximos()) {
            log.warn("[VALIDACION FALLIDA] Cupon sin stock de usos disponibles.");
            throw new IllegalArgumentException("El cupon ha superado el limite maximo de usos permitidos.");
        }

        if (validacion.getTotalOrden() < promo.getMontoMinimo()) {
            throw new IllegalArgumentException("El total de la orden no alcanza el monto minimo requerido de $" + promo.getMontoMinimo());
        }

        if (promo.getValor() >= validacion.getTotalOrden()) {
            log.error("[VALIDACION FALLIDA] El valor del descuento supera o iguala al total de la orden.");
            throw new IllegalArgumentException("El monto del descuento no puede ser superior o igual al total de la orden.");
        }

        promo.setUsosActuales(promo.getUsosActuales() + 1);
        if (promo.getUsosActuales().equals(promo.getUsosMaximos())) {
            promo.setEstado("AGOTADO");
        }

        return promocionRepository.save(promo);
    }

    public List<Promocion> listarTodas() {
        return promocionRepository.findAll();
    }

    public List<Promocion> listarPorEstado(String estado) {
        return promocionRepository.findByEstado(estado.toUpperCase());
    }

    public Promocion buscarPorId(Long id) {
        return promocionRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontro ninguna promocion con el ID: " + id));
    }

    public Promocion buscarPorCodigo(String codigo) {
        return promocionRepository.findByCodigo(codigo.toUpperCase())
                .orElseThrow(() -> new IllegalArgumentException("No se encontro ninguna promocion con el codigo: " + codigo));
    }

    @Transactional
    public Promocion actualizarFechasYCondiciones(Long id, LocalDate nuevaFechaFin, Integer nuevosUsosMax) {
        log.info("[AUDITORIA] Modificando condiciones de la promocion ID: {}", id);
        Promocion promo = buscarPorId(id);

        if (nuevaFechaFin != null) {
            if (nuevaFechaFin.isBefore(promo.getFechaInicio())) {
                throw new IllegalArgumentException("La nueva fecha de fin no puede ser menor a la fecha de inicio original.");
            }
            promo.setFechaFin(nuevaFechaFin);
        }

        if (nuevosUsosMax != null) {
            if (nuevosUsosMax < promo.getUsosActuales()) {
                throw new IllegalArgumentException("Los usos maximos no pueden ser menores a la cantidad de usos ya efectuados.");
            }
            promo.setUsosMaximos(nuevosUsosMax);
            if (promo.getUsosActuales() < nuevosUsosMax && "AGOTADO".equals(promo.getEstado())) {
                promo.setEstado("ACTIVO");
            }
        }

        return promocionRepository.save(promo);
    }

    @Transactional
    public void desactivarPromocion(Long id) {
        log.info("[AUDITORIA] Desactivando cupon ID: {}", id);
        Promocion promo = buscarPorId(id);
        promo.setEstado("INACTIVO");
        promocionRepository.save(promo);
    }
}