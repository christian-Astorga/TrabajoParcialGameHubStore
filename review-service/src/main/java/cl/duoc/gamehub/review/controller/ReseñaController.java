package cl.duoc.gamehub.review.controller;

import cl.duoc.gamehub.review.dto.ReviewRequestDTO;
import cl.duoc.gamehub.review.model.Reseña;
import cl.duoc.gamehub.review.service.ReseñaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reseñas")
public class ReseñaController {

    private final ReseñaService resenaService;

    public ReseñaController(ReseñaService resenaService) {
        this.resenaService = resenaService;
    }

    @PostMapping("/crear")
    public ResponseEntity<Reseña> crearResena(@Valid @RequestBody ReviewRequestDTO request) {
        Reseña nueva = resenaService.crearResena(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nueva);
    }

    @GetMapping("/producto/{productoId}")
    public ResponseEntity<List<Reseña>> listarPorProducto(@PathVariable Long productoId) {
        return ResponseEntity.ok(resenaService.listarPorProducto(productoId));
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Reseña>> listarPorUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(resenaService.listarPorUsuario(usuarioId));
    }

    @GetMapping("/buscar/{id}")
    public ResponseEntity<Reseña> buscarPorId(@PathVariable Long id) {
        return ResponseEntity.ok(resenaService.buscarPorId(id));
    }

    @PutMapping("/actualizar/{id}")
    public ResponseEntity<Reseña> actualizarResena(
            @PathVariable Long id,
            @RequestParam(required = false) Integer nuevaPuntuacion,
            @RequestParam(required = false) String nuevoComentario) {
        return ResponseEntity.ok(resenaService.actualizarComentario(id, nuevaPuntuacion, nuevoComentario));
    }

    @PutMapping("/moderar/{id}")
    public ResponseEntity<Void> moderarResena(@PathVariable Long id) {
        resenaService.moderarOEliminarResena(id);
        return ResponseEntity.noContent().build();
    }
}