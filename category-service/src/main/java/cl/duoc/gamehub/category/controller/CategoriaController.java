package cl.duoc.gamehub.category.controller;

import cl.duoc.gamehub.category.dto.CategoriaDTO;
import cl.duoc.gamehub.category.model.Categoria;
import cl.duoc.gamehub.category.service.CategoriaService;
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
@RequestMapping("/api/categorias")
@Tag(name = "Categorías", description = "Endpoints para la gestión de clasificaciones del catálogo comercial")
public class CategoriaController {

    @Autowired
    private CategoriaService categoriaService;

    @PostMapping("/crear")
    @Operation(summary = "Crear Categoría", description = "Registra una nueva categoría procesando un DTO")
    public ResponseEntity<EntityModel<Categoria>> crear(@Valid @RequestBody CategoriaDTO dto) {
        Categoria nueva = categoriaService.crearCategoria(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapearHateoas(nueva));
    }

    @GetMapping("/listar")
    @Operation(summary = "Listar todas las categorías", description = "Retorna el listado completo con enlaces hipermedia")
    public ResponseEntity<List<EntityModel<Categoria>>> listar() {
        List<EntityModel<Categoria>> lista = categoriaService.listarTodas().stream()
                .map(this::mapearHateoas)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/buscar/{id}")
    @Operation(summary = "Buscar categoría por ID", description = "Endpoint clave")
    public ResponseEntity<EntityModel<Categoria>> buscarPorId(@PathVariable Long id) {
        Categoria categoria = categoriaService.buscarPorId(id);
        return ResponseEntity.ok(mapearHateoas(categoria));
    }

    @PutMapping("/actualizar/{id}")
    @Operation(summary = "Actualizar categoría por ID")
    public ResponseEntity<EntityModel<Categoria>> actualizar(@PathVariable Long id, @Valid @RequestBody CategoriaDTO dto) {
        Categoria actualizada = categoriaService.actualizarCategoria(id, dto);
        return ResponseEntity.ok(mapearHateoas(actualizada));
    }

    @DeleteMapping("/desactivar/{id}")
    @Operation(summary = "Desactivación lógica", description = "Cambia el estado de la categoría a INACTIVO")
    public ResponseEntity<EntityModel<Categoria>> desactivar(@PathVariable Long id) {
        Categoria desactivada = categoriaService.desactivarCategoria(id);
        return ResponseEntity.ok(mapearHateoas(desactivada));
    }


    private EntityModel<Categoria> mapearHateoas(Categoria categoria) {
        Link selfLink = linkTo(methodOn(CategoriaController.class).buscarPorId(categoria.getId())).withSelfRel();
        Link listarLink = linkTo(methodOn(CategoriaController.class).listar()).withRel("lista_completa");
        return EntityModel.of(categoria, selfLink, listarLink);
    }
}