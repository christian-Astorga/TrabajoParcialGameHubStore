package cl.duoc.gamehub.shipping.controller;

import cl.duoc.gamehub.shipping.dto.ShippingRequestDTO;
import cl.duoc.gamehub.shipping.model.Despacho;
import cl.duoc.gamehub.shipping.service.DespachoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/despachos")
public class DespachoController {

    private final DespachoService despachoService;

    public DespachoController(DespachoService despachoService) {
        this.despachoService = despachoService;
    }

    @PostMapping("/crear")
    public ResponseEntity<Despacho> crearDespacho(@Valid @RequestBody ShippingRequestDTO request) {
        Despacho nuevo = despachoService.crearDespacho(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevo);
    }

    @GetMapping("/listar")
    public ResponseEntity<List<Despacho>> listarTodos() {
        return ResponseEntity.ok(despachoService.listarTodos());
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<Despacho> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(despachoService.buscarPorId(id));
    }

    @GetMapping("/orden/{ordenId}")
    public ResponseEntity<List<Despacho>> listarPorOrden(@PathVariable Long ordenId) {
        return ResponseEntity.ok(despachoService.listarPorOrden(ordenId));
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Despacho>> listarPorEstado(@PathVariable String estado) {
        return ResponseEntity.ok(despachoService.listarPorEstado(estado));
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Despacho> actualizarDespacho(
            @PathVariable Long id,
            @RequestParam String nuevoEstado,
            @RequestParam(required = false) String nuevoTracking) {
        return ResponseEntity.ok(despachoService.actualizarEstadoYTracking(id, nuevoEstado, nuevoTracking));
    }

    @PutMapping("/cancelar/{id}")
    public ResponseEntity<Void> cancelarDespacho(@PathVariable Long id) {
        despachoService.cancelarDespacho(id);
        return ResponseEntity.noContent().build();
    }
}