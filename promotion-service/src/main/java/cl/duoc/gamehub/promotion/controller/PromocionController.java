package cl.duoc.gamehub.promotion.controller;

import cl.duoc.gamehub.promotion.dto.PromotionRequestDTO;
import cl.duoc.gamehub.promotion.dto.ValidateCouponDTO;
import cl.duoc.gamehub.promotion.model.Promocion;
import cl.duoc.gamehub.promotion.service.PromocionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/promociones")
@Tag(name = "Promociones", description = "Endpoints para el ciclo de vida y validación de cupones de descuento en GameHub Store")
public class PromocionController {

    private final PromocionService promocionService;

    public PromocionController(PromocionService promocionService) {
        this.promocionService = promocionService;
    }

    @PostMapping("/crear")
    @Operation(summary = "Crear nueva promoción/cupón")
    public ResponseEntity<EntityModel<Promocion>> crearPromocion(@Valid @RequestBody PromotionRequestDTO request) {
        Promocion nueva = promocionService.crearPromocion(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapearHateoas(nueva));
    }

    @PostMapping("/validar-aplicar")
    @Operation(summary = "Validar y aplicar un cupón a una orden")
    public ResponseEntity<EntityModel<Promocion>> validarYAplicar(@Valid @RequestBody ValidateCouponDTO validacion) {
        Promocion aplicada = promocionService.validarYAplicarCupon(validacion);
        return ResponseEntity.ok(mapearHateoas(aplicada));
    }

    @GetMapping("/listar")
    @Operation(summary = "Listar todas las promociones registradas")
    public ResponseEntity<List<EntityModel<Promocion>>> listarTodas() {
        List<EntityModel<Promocion>> lista = promocionService.listarTodas().stream()
                .map(this::mapearHateoas)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Filtrar promociones por su estado")
    public ResponseEntity<List<EntityModel<Promocion>>> listarPorEstado(@PathVariable String estado) {
        List<EntityModel<Promocion>> lista = promocionService.listarPorEstado(estado).stream()
                .map(this::mapearHateoas)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/buscar/{id}")
    @Operation(summary = "Buscar una promoción por su ID")
    public ResponseEntity<EntityModel<Promocion>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mapearHateoas(promocionService.buscarPorId(id)));
    }

    @GetMapping("/codigo/{codigo}")
    @Operation(summary = "Buscar una promoción por su código de cupón")
    public ResponseEntity<EntityModel<Promocion>> buscarPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(mapearHateoas(promocionService.buscarPorCodigo(codigo)));
    }

    @PutMapping("/actualizar/{id}")
    @Operation(summary = "Actualizar fechas vigentes y límites de uso de un cupón")
    public ResponseEntity<EntityModel<Promocion>> actualizarPromocion(
            @PathVariable Long id,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate nuevaFechaFin,
            @RequestParam(required = false) Integer nuevosUsosMax) {
        Promocion modificada = promocionService.actualizarFechasYCondiciones(id, nuevaFechaFin, nuevosUsosMax);
        return ResponseEntity.ok(mapearHateoas(modificada));
    }

    @PutMapping("/desactivar/{id}")
    @Operation(summary = "Desactivar de forma lógica una promoción")
    public ResponseEntity<Void> desactivarPromocion(@PathVariable Long id) {
        promocionService.desactivarPromocion(id);
        return ResponseEntity.noContent().build();
    }

    // Método HATEOAS correctamente integrado dentro de la clase
    private EntityModel<Promocion> mapearHateoas(Promocion promo) {
        Link selfLink = linkTo(methodOn(PromocionController.class).buscarPorId(promo.getId())).withSelfRel();
        Link buscarCodigoLink = linkTo(methodOn(PromocionController.class).buscarPorCodigo(promo.getCodigo())).withRel("buscar_por_codigo");
        Link listarLink = linkTo(methodOn(PromocionController.class).listarTodas()).withRel("historial_promociones");
        return EntityModel.of(promo, selfLink, buscarCodigoLink, listarLink);
    }
}