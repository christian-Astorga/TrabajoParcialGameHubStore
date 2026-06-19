package cl.duoc.gamehub.review.controller;

import cl.duoc.gamehub.review.dto.ReviewRequestDTO;
import cl.duoc.gamehub.review.model.Reseña;
import cl.duoc.gamehub.review.service.ReseñaService;
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
@RequestMapping("/api/reseñas")
@Tag(name = "Reseñas y Calificaciones", description = "Endpoints para gestionar el feedback de los videojuegos adquiridos en GameHub Store")
public class ReseñaController {

    private final ReseñaService resenaService;

    public ReseñaController(ReseñaService resenaService) {
        this.resenaService = resenaService;
    }

    @PostMapping("/crear")
    @Operation(summary = "Crear Reseña", description = "Registra una nueva opinión validando que el cliente haya comprado el juego a través de order-service")
    public ResponseEntity<EntityModel<Reseña>> crearResena(@Valid @RequestBody ReviewRequestDTO request) {
        Reseña nueva = resenaService.crearResena(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(mapearHateoas(nueva));
    }

    @GetMapping("/producto/{productoId}")
    @Operation(summary = "Listar por Producto", description = "Recupera todas las reseñas asociadas a un ID de videojuego específico")
    public ResponseEntity<List<EntityModel<Reseña>>> listarPorProducto(@PathVariable Long productoId) {
        List<EntityModel<Reseña>> lista = resenaService.listarPorProducto(productoId).stream()
                .map(this::mapearHateoas)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/usuario/{usuarioId}")
    @Operation(summary = "Listar por Usuario", description = "Obtiene el historial completo de opiniones escritas por un jugador")
    public ResponseEntity<List<EntityModel<Reseña>>> listarPorUsuario(@PathVariable Long usuarioId) {
        List<EntityModel<Reseña>> lista = resenaService.listarPorUsuario(usuarioId).stream()
                .map(this::mapearHateoas)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/buscar/{id}")
    @Operation(summary = "Buscar Reseña por ID")
    public ResponseEntity<EntityModel<Reseña>> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(mapearHateoas(resenaService.buscarPorId(id)));
    }

    @PutMapping("/actualizar/{id}")
    @Operation(summary = "Actualizar Comentario", description = "Permite al usuario editar su puntuación y las observaciones de una reseña previa")
    public ResponseEntity<EntityModel<Reseña>> actualizarResena(
            @PathVariable Long id,
            @RequestParam(required = false) Integer nuevaPuntuacion,
            @RequestParam(required = false) String nuevoComentario) {
        Reseña modificada = resenaService.actualizarComentario(id, nuevaPuntuacion, nuevoComentario);
        return ResponseEntity.ok(mapearHateoas(modificada));
    }

    @PutMapping("/moderar/{id}")
    @Operation(summary = "Moderar Reseña", description = "Cambia el estado de la reseña a MODERADO para ocultarla del catálogo por conductas inapropiadas")
    public ResponseEntity<Void> moderarResena(@PathVariable Long id) {
        resenaService.moderarOEliminarResena(id);
        return ResponseEntity.noContent().build();
    }

    // Inyección de Enlaces Hipermedia HATEOAS oficiales
    private EntityModel<Reseña> mapearHateoas(Reseña resena) {
        Link selfLink = linkTo(methodOn(ReseñaController.class).buscarPorId(resena.getId())).withSelfRel();
        Link prodLink = linkTo(methodOn(ReseñaController.class).listarPorProducto(resena.getProductoId())).withRel("opiniones_videojuego");
        Link userLink = linkTo(methodOn(ReseñaController.class).listarPorUsuario(resena.getUsuarioId())).withRel("historial_autor");
        return EntityModel.of(resena, selfLink, prodLink, userLink);
    }
}