package cl.duoc.gamehub.inventory.controller;

import cl.duoc.gamehub.inventory.dto.InventarioDTO;
import cl.duoc.gamehub.inventory.dto.ReservaDTO;
import cl.duoc.gamehub.inventory.model.Inventario;
import cl.duoc.gamehub.inventory.service.InventarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/inventario")
@Tag(name = "Inventario", description = "Endpoints para el control de stock y reservas temporales de productos")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    @PostMapping("/crear")
    @Operation(summary = "Crear registro de stock inicial", description = "Inicializa el inventario de un producto validando su existencia mediante Feign")
    public ResponseEntity<EntityModel<Inventario>> crearRegistroStock(@Valid @RequestBody InventarioDTO dto) {
        Inventario nuevo = inventarioService.crearRegistroStock(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapearHateoas(nuevo));
    }

    @GetMapping("/listar")
    @Operation(summary = "Listar todos los registros de inventario")
    public ResponseEntity<List<EntityModel<Inventario>>> listarTodo() {
        List<EntityModel<Inventario>> lista = inventarioService.listarTodo().stream()
                .map(this::mapearHateoas)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/buscar/{id}")
    @Operation(summary = "Buscar stock por ID de registro")
    public ResponseEntity<EntityModel<Inventario>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mapearHateoas(inventarioService.buscarPorId(id)));
    }

    @GetMapping("/producto/{productoId}")
    @Operation(summary = "Buscar stock por ID del producto", description = "Endpoint estratégico para ser consumido externamente")
    public ResponseEntity<EntityModel<Inventario>> buscarPorProductoId(@PathVariable Long productoId) {
        return ResponseEntity.ok(mapearHateoas(inventarioService.buscarPorProductoId(productoId)));
    }

    @PutMapping("/reservar")
    @Operation(summary = "Reservar stock temporal", description = "Mueve unidades de disponible a reservado al iniciar un flujo de compra")
    public ResponseEntity<EntityModel<Inventario>> reservarStockTemporal(@Valid @RequestBody ReservaDTO dto) {
        return ResponseEntity.ok(mapearHateoas(inventarioService.reservarStockTemporal(dto)));
    }

    @PutMapping("/actualizar/{productoId}")
    @Operation(summary = "Actualizar cantidades manualmente", description = "Modifica el stock disponible generando automáticamente un movimiento Kárdex")
    public ResponseEntity<EntityModel<Inventario>> actualizarCantidades(
            @PathVariable Long productoId,
            @RequestParam Integer stockDisponible) {
        return ResponseEntity.ok(mapearHateoas(inventarioService.actualizarCantidades(productoId, stockDisponible)));
    }

    @DeleteMapping("/eliminar/{id}")
    @Operation(summary = "Eliminar registro de stock", description = "Baja física de un registro de inventario obsoleto dejando rastro histórico")
    public ResponseEntity<Void> eliminarRegistroStockObsoleto(@PathVariable Long id) {
        inventarioService.eliminarRegistroStockObsoleto(id);
        return ResponseEntity.noContent().build();
    }

    // Constructor de enlaces hipermedia exigido por la pauta
    private EntityModel<Inventario> mapearHateoas(Inventario inventario) {
        Link selfLink = linkTo(methodOn(InventarioController.class).buscarPorId(inventario.getId())).withSelfRel();
        Link prodLink = linkTo(methodOn(InventarioController.class).buscarPorProductoId(inventario.getProductoId())).withRel("stock_por_producto");
        Link listarLink = linkTo(methodOn(InventarioController.class).listarTodo()).withRel("lista_inventarios");
        return EntityModel.of(inventario, selfLink, prodLink, listarLink);
    }
}