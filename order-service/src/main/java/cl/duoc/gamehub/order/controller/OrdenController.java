package cl.duoc.gamehub.order.controller;

import cl.duoc.gamehub.order.dto.OrdenDTO;
import cl.duoc.gamehub.order.model.Orden;
import cl.duoc.gamehub.order.service.OrdenService;
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
@RequestMapping("/api/ordenes")
@Tag(name = "Órdenes", description = "Controlador centralizado para la orquestación y flujo de compras de GameHub")
public class OrdenController {

    @Autowired
    private OrdenService ordenService;

    @PostMapping("/crear")
    @Operation(summary = "Crear orden de compra", description = "Orquesta llamadas cruzadas por OpenFeign para validar el precio del producto y reservar existencias")
    public ResponseEntity<EntityModel<Orden>> crearOrden(@Valid @RequestBody OrdenDTO dto) {
        Orden nuevaOrden = ordenService.crearOrden(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapearHateoas(nuevaOrden));
    }

    @GetMapping("/listar")
    @Operation(summary = "Listar todas las órdenes generales")
    public ResponseEntity<List<EntityModel<Orden>>> listarTodas() {
        List<EntityModel<Orden>> lista = ordenService.listarTodas().stream()
                .map(this::mapearHateoas)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/cliente/{usuarioId}")
    @Operation(summary = "Listar órdenes de un cliente específico")
    public ResponseEntity<List<EntityModel<Orden>>> listarPorCliente(@PathVariable Long usuarioId) {
        List<EntityModel<Orden>> lista = ordenService.listarPorCliente(usuarioId).stream()
                .map(this::mapearHateoas)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/estado")
    @Operation(summary = "Listar órdenes filtradas por estado")
    public ResponseEntity<List<EntityModel<Orden>>> listarPorEstado(@RequestParam String estado) {
        List<EntityModel<Orden>> lista = ordenService.listarPorEstado(estado).stream()
                .map(this::mapearHateoas)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/buscar/{id}")
    @Operation(summary = "Buscar orden por su ID único")
    public ResponseEntity<EntityModel<Orden>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mapearHateoas(ordenService.buscarPorId(id)));
    }

    @PutMapping("/actualizar-estado/{id}")
    @Operation(summary = "Actualizar estado de la orden", description = "Modifica estados aplicando restricciones de negocio para flujos ya finalizados")
    public ResponseEntity<EntityModel<Orden>> actualizarEstado(
            @PathVariable Long id,
            @RequestParam String estado) {
        return ResponseEntity.ok(mapearHateoas(ordenService.actualizarEstado(id, estado)));
    }

    @PutMapping("/cancelar/{id}")
    @Operation(summary = "Cancelar orden de compra", description = "Dada de baja una orden pendiente y devuelve las unidades reservadas al stock de inventario")
    public ResponseEntity<Void> cancelarOrden(@PathVariable Long id) {
        ordenService.cancelarOrden(id);
        return ResponseEntity.noContent().build();
    }

    // Constructor de hipermedios HATEOAS para la rúbrica
    private EntityModel<Orden> mapearHateoas(Orden orden) {
        Link selfLink = linkTo(methodOn(OrdenController.class).buscarPorId(orden.getId())).withSelfRel();
        Link clienteLink = linkTo(methodOn(OrdenController.class).listarPorCliente(orden.getUsuarioId())).withRel("ordenes_del_cliente");
        Link listarLink = linkTo(methodOn(OrdenController.class).listarTodas()).withRel("lista_general");
        return EntityModel.of(orden, selfLink, clienteLink, listarLink);
    }
}