package cl.duoc.gamehub.inventory.controller;

import cl.duoc.gamehub.inventory.dto.InventarioDTO;
import cl.duoc.gamehub.inventory.dto.ReservaDTO;
import cl.duoc.gamehub.inventory.model.Inventario;
import cl.duoc.gamehub.inventory.service.InventarioService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventario")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    // 1. Crear registro de stock inicial para un producto
    @PostMapping("/crear")
    public ResponseEntity<Inventario> crearRegistroStock(@Valid @RequestBody InventarioDTO dto) {
        Inventario nuevo = inventarioService.crearRegistroStock(dto);
        return new ResponseEntity<>(nuevo, HttpStatus.CREATED);
    }
    // 2. Listar todos los registros de inventario
    @GetMapping("/listar")
    public ResponseEntity<List<Inventario>> listarTodo() {
        return ResponseEntity.ok(inventarioService.listarTodo());
    }
    // 3. Buscar stock por ID del registro de inventario
    @GetMapping("/buscar/{id}")
    public ResponseEntity<Inventario> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(inventarioService.buscarPorId(id));
    }
    // 4. Buscar stock directamente usando el ID del producto
    @GetMapping("/producto/{productoId}")
    public ResponseEntity<Inventario> buscarPorProductoId(@PathVariable Long productoId) {
        return ResponseEntity.ok(inventarioService.buscarPorProductoId(productoId));
    }
    // 5. Reservar stock temporal
    @PutMapping("/reservar")
    public ResponseEntity<Inventario> reservarStockTemporal(@Valid @RequestBody ReservaDTO dto) {
        return ResponseEntity.ok(inventarioService.reservarStockTemporal(dto));
    }
    // 6. Actualizar cantidades disponibles manualmente
    @PutMapping("/actualizar/{productoId}")
    public ResponseEntity<Inventario> actualizarCantidades(
            @PathVariable Long productoId,
            @RequestParam Integer stockDisponible) {
        return ResponseEntity.ok(inventarioService.actualizarCantidades(productoId, stockDisponible));
    }
    // 7. Eliminar o cerrar registro de stock obsoleto
    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<Void> eliminarRegistroStockObsoleto(@PathVariable Long id) {
        inventarioService.eliminarRegistroStockObsoleto(id);
        return ResponseEntity.noContent().build();
    }
}