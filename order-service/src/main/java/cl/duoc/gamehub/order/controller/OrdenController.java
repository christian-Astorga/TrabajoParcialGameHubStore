package cl.duoc.gamehub.order.controller;

import cl.duoc.gamehub.order.dto.OrdenDTO;
import cl.duoc.gamehub.order.model.Orden;
import cl.duoc.gamehub.order.service.OrdenService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ordenes")
public class OrdenController {

    @Autowired
    private OrdenService ordenService;

    // 1 Crear orden (Valida stock y precios cruzados por OpenFeign)
    @PostMapping("/crear")
    public ResponseEntity<Orden> crearOrden(@Valid @RequestBody OrdenDTO dto) {
        Orden nuevaOrden = ordenService.crearOrden(dto);
        return new ResponseEntity<>(nuevaOrden, HttpStatus.CREATED);
    }
    // 2 Listar todas las órdenes generales
    @GetMapping("/listar")
    public ResponseEntity<List<Orden>> listarTodas() {
        return ResponseEntity.ok(ordenService.listarTodas());
    }
    // 3 Listar órdenes específicas de un Cliente
    @GetMapping("/cliente/{usuarioId}")
    public ResponseEntity<List<Orden>> listarPorCliente(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(ordenService.listarPorCliente(usuarioId));
    }
    // 4 Listar órdenes filtradas por Estado
    @GetMapping("/estado")
    public ResponseEntity<List<Orden>> listarPorEstado(@RequestParam String estado) {
        return ResponseEntity.ok(ordenService.listarPorEstado(estado));
    }
    // 5 Buscar orden por su ID único
    @GetMapping("/buscar/{id}")
    public ResponseEntity<Orden> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(ordenService.buscarPorId(id));
    }
    // 6 Actualizar estado de la orden
    @PutMapping("/actualizar-estado/{id}")
    public ResponseEntity<Orden> actualizarEstado(
            @PathVariable Long id,
            @RequestParam String estado) {
        return ResponseEntity.ok(ordenService.actualizarEstado(id, estado));
    }
    // 7 Cancelar orden
    @PutMapping("/cancelar/{id}")
    public ResponseEntity<Void> cancelarOrden(@PathVariable Long id) {
        ordenService.cancelarOrden(id);
        return ResponseEntity.noContent().build();
    }
}