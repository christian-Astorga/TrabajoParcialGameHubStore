package cl.duoc.gamehub.category.service;

import cl.duoc.gamehub.category.dto.CategoriaDTO;
import cl.duoc.gamehub.category.model.Categoria;
import cl.duoc.gamehub.category.repository.CategoriaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class CategoriaService {

    private static final Logger log = LoggerFactory.getLogger(CategoriaService.class);

    @Autowired
    private CategoriaRepository categoriaRepository;

    public Categoria crearCategoria(CategoriaDTO dto) {
        log.info("[CATEGORY-SERVICE] Intentando registrar una nueva categoría comercial: {}", dto.getNombre());

        String nombreNormalizado = dto.getNombre().trim().toUpperCase();
        categoriaRepository.findByNombre(nombreNormalizado).ifPresent(c -> {
            log.error("[CATEGORY-SERVICE] Error de validación: La categoría '{}' ya existe.", nombreNormalizado);
            throw new RuntimeException("El nombre de la categoría ya existe.");
        });

        Categoria categoria = new Categoria();
        categoria.setNombre(nombreNormalizado);
        categoria.setDescripcion(dto.getDescripcion());
        categoria.setEstado("ACTIVO");

        log.info("[CATEGORY-SERVICE] Categoría '{}' creada de forma exitosa.", nombreNormalizado);
        return categoriaRepository.save(categoria);
    }

    public List<Categoria> listarTodas() {
        log.info("[CATEGORY-SERVICE] Solicitando listado completo de categorías.");
        return categoriaRepository.findAll();
    }

    public Categoria buscarPorId(Long id) {
        log.info("[CATEGORY-SERVICE] Buscando categoría con ID: {}", id);
        return categoriaRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("[CATEGORY-SERVICE] Error: Categoría con ID {} no existe.", id);
                    return new RuntimeException("Categoría no encontrada.");
                });
    }

    public Categoria actualizarCategoria(Long id, CategoriaDTO dto) {
        log.info("[CATEGORY-SERVICE] Solicitud recibida para modificar categoría ID: {}", id);
        Categoria categoria = buscarPorId(id);

        String nuevoNombre = dto.getNombre().trim().toUpperCase();

        if (!categoria.getNombre().equals(nuevoNombre)) {
            categoriaRepository.findByNombre(nuevoNombre).ifPresent(c -> {
                log.error("[CATEGORY-SERVICE] Error: El nombre '{}' ya está ocupado.", nuevoNombre);
                throw new RuntimeException("El nombre de la categoría ya existe.");
            });
        }

        categoria.setNombre(nuevoNombre);
        categoria.setDescripcion(dto.getDescripcion());
        if (dto.getEstado() != null && !dto.getEstado().isBlank()) {
            categoria.setEstado(dto.getEstado().toUpperCase());
        }

        log.info("[CATEGORY-SERVICE] Categoría ID {} actualizada correctamente.", id);
        return categoriaRepository.save(categoria);
    }

    public Categoria desactivarCategoria(Long id) {
        log.warn("[CATEGORY-SERVICE] Petición de desactivación lógica para categoría ID: {}", id);
        Categoria categoria = buscarPorId(id);

        // Simulación control de negocio (aquí se enganchará OpenFeign después)
        categoria.setEstado("INACTIVO");
        log.info("[CATEGORY-SERVICE] Categoría ID {} pasa a estado INACTIVO.", id);
        return categoriaRepository.save(categoria);
    }
}