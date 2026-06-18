package cl.duoc.gamehub.product.controller;

import cl.duoc.gamehub.product.model.Producto;
import cl.duoc.gamehub.product.service.ProductoService;
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
@RequestMapping("/api/productos")
@Tag(name = "Productos", description = "Endpoints para la gestión del catálogo de artículos gamer")
public class ProductoController {

    @Autowired
    private ProductoService service;

    @PostMapping("/crear")
    @Operation(summary = "Crear Producto", description = "Registra un producto validando su categoría mediante OpenFeign")
    public ResponseEntity<EntityModel<Producto>> crear(@Valid @RequestBody Producto producto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(mapearHateoas(service.crear(producto)));
    }

    @GetMapping("/listar")
    @Operation(summary = "Listar catálogo de productos")
    public ResponseEntity<List<EntityModel<Producto>>> listar() {
        List<EntityModel<Producto>> lista = service.listarTodos().stream()
                .map(this::mapearHateoas)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/buscar/{id}")
    @Operation(summary = "Buscar por ID")
    public ResponseEntity<EntityModel<Producto>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mapearHateoas(service.buscarPorId(id)));
    }

    @GetMapping("/categoria/{categoriaId}")
    @Operation(summary = "Listar por Categoría")
    public ResponseEntity<List<EntityModel<Producto>>> listarPorCategoria(@PathVariable Long categoriaId) {
        List<EntityModel<Producto>> lista = service.listarPorCategoria(categoriaId).stream()
                .map(this::mapearHateoas)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @PutMapping("/actualizar/{id}")
    @Operation(summary = "Actualizar datos de producto")
    public ResponseEntity<EntityModel<Producto>> actualizar(@PathVariable Long id, @RequestBody Producto producto) {
        return ResponseEntity.ok(mapearHateoas(service.actualizar(id, producto)));
    }

    @DeleteMapping("/desactivar/{id}")
    @Operation(summary = "Baja lógica de producto")
    public ResponseEntity<EntityModel<Producto>> desactivar(@PathVariable Long id) {
        return ResponseEntity.ok(mapearHateoas(service.desactivar(id)));
    }

    private EntityModel<Producto> mapearHateoas(Producto producto) {
        Link selfLink = linkTo(methodOn(ProductoController.class).buscarPorId(producto.getId())).withSelfRel();
        Link categoryLink = linkTo(methodOn(ProductoController.class).listarPorCategoria(producto.getCategoriaId())).withRel("productos_misma_categoria");
        Link listarLink = linkTo(methodOn(ProductoController.class).listar()).withRel("catalogo_completo");

        return EntityModel.of(producto, selfLink, categoryLink, listarLink);
    }
}