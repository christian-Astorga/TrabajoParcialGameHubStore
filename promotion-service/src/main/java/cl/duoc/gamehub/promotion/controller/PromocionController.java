package cl.duoc.gamehub.promotion.controller;

import cl.duoc.gamehub.promotion.dto.PromotionRequestDTO;
import cl.duoc.gamehub.promotion.dto.ValidateCouponDTO;
import cl.duoc.gamehub.promotion.model.Promocion;
import cl.duoc.gamehub.promotion.service.PromocionService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/promociones")
public class PromocionController {

    private final PromocionService promocionService;

    public PromocionController(PromocionService promocionService) {
        this.promocionService = promocionService;
    }

    @PostMapping("/crear")
    public ResponseEntity<Promocion> crearPromocion(@Valid @RequestBody PromotionRequestDTO request) {
        Promocion nueva = promocionService.crearPromocion(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    @PostMapping("/validar-aplicar")
    public ResponseEntity<Promocion> validarYAplicar(@Valid @RequestBody ValidateCouponDTO validacion) {
        return ResponseEntity.ok(promocionService.validarYAplicarCupon(validacion));
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Promocion>> listarTodas() {
        return ResponseEntity.ok(promocionService.listarTodas());
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Promocion>> listarPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(promocionService.listarPorEstado(estado));
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<Promocion> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(promocionService.buscarPorId(id));
    }

    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<Promocion> buscarPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(promocionService.buscarPorCodigo(codigo));
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Promocion> actualizarPromocion(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate nuevaFechaFin,
            @RequestParam(required = false) Integer nuevosUsosMax) {
        return ResponseEntity.ok(promocionService.actualizarFechasYCondiciones(id, nuevaFechaFin, nuevosUsosMax));
    }

    @PutMapping("/desactivar/{id}")
    public ResponseEntity<Void> desactivarPromocion(@PathVariable Long id) {
        promocionService.desactivarPromocion(id);
        return ResponseEntity.noContent().build();
    }
}