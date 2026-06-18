package cl.duoc.gamehub.shipping.controller;

import cl.duoc.gamehub.shipping.dto.ShippingRequestDTO;
import cl.duoc.gamehub.shipping.model.Despacho;
import cl.duoc.gamehub.shipping.service.DespachoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/despachos")
@Tag(name = "Despachos", description = "Endpoints para la gestión logística y tracking de GameHub Store")
public class DespachoController {

    private final DespachoService despachoService;

    public DespachoController(DespachoService despachoService) {
        this.despachoService = despachoService;
    }

    @PostMapping("/crear")
    @Operation(summary = "Crear Despacho", description = "Genera una guía logística solo si la orden consultada por OpenFeign está PAGADA")
    public ResponseEntity<EntityModel<Despacho>> crearDespacho(@Valid @RequestBody ShippingRequestDTO request) {
        Despacho nuevo = despachoService.crearDespacho(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapearHateoas(nuevo));
    }

    @GetMapping("/listar")
    @Operation(summary = "Listar todos los despachos")
    public ResponseEntity<List<EntityModel<Despacho>>> listarTodos() {
        List<EntityModel<Despacho>> lista = despachoService.listarTodos().stream()
                .map(this::mapearHateoas)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/buscar/{id}")
    @Operation(summary = "Buscar despacho por su ID")
    public ResponseEntity<EntityModel<Despacho>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mapearHateoas(despachoService.buscarPorId(id)));
    }

    @GetMapping("/orden/{ordenId}")
    @Operation(summary = "Buscar despachos asociados a una Orden ID")
    public ResponseEntity<List<EntityModel<Despacho>>> listarPorOrden(@PathVariable Long ordenId) {
        List<EntityModel<Despacho>> lista = despachoService.listarPorOrden(ordenId).stream()
                .map(this::mapearHateoas)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/estado/{estado}")
    @Operation(summary = "Listar despachos por estado (PREPARACION, EN_CAMINO, DELIVERED, CANCELADO)")
    public ResponseEntity<List<EntityModel<Despacho>>> listarPorEstado(@PathVariable String estado) {
        List<EntityModel<Despacho>> lista = despachoService.listarPorEstado(estado).stream()
                .map(this::mapearHateoas)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @PutMapping("/actualizar/{id}")
    @Operation(summary = "Actualizar estado y tracking logístico")
    public ResponseEntity<EntityModel<Despacho>> actualizarDespacho(
            @PathVariable Long id,
            @RequestParam String nuevoEstado,
            @RequestParam(required = false) String nuevoTracking) {
        Despacho modificado = despachoService.actualizarEstadoYTracking(id, nuevoEstado, nuevoTracking);
        return ResponseEntity.ok(mapearHateoas(modificado));
    }

    @PutMapping("/cancelar/{id}")
    @Operation(summary = "Anulación lógica del despacho")
    public ResponseEntity<Void> cancelarDespacho(@PathVariable Long id) {
        despachoService.cancelarDespacho(id);
        return ResponseEntity.noContent().build();
    }

    // Constructor de Enlaces Hipermedia HATEOAS Exigido por Rúbrica
    private EntityModel<Despacho> mapearHateoas(Despacho despacho) {
        Link selfLink = linkTo(methodOn(DespachoController.class).buscarPorId(despacho.getId())).withSelfRel();
        Link ordenLink = linkTo(methodOn(DespachoController.class).listarPorOrden(despacho.getOrdenId())).withRel("despachos_mismo_pedido");
        Link historialLink = linkTo(methodOn(DespachoController.class).listarTodos()).withRel("historial_despachos");
        return EntityModel.of(despacho, selfLink, ordenLink, historialLink);
    }
}