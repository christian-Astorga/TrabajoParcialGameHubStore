package cl.duoc.gamehub.review.dto;

import jakarta.validation.constraints.*;

public class ReviewRequestDTO {

    @NotNull(message = "El ID del usuario es obligatorio.")
    private Long usuarioId;

    @NotNull(message = "El ID del producto es obligatorio.")
    private Long productoId;

    @NotNull(message = "El ID de la orden de compra es obligatorio.")
    private Long ordenId;

    @NotNull(message = "La puntuacion es obligatoria.")
    @Min(value = 1, message = "La puntuacion minima es de 1 estrella.")
    @Max(value = 5, message = "La puntuacion maxima es de 5 estrellas.")
    private Integer puntuacion;

    @NotBlank(message = "El comentario no puede estar vacio.")
    @Size(min = 10, max = 500, message = "El comentario debe tener entre 10 y 500 caracteres.")
    private String comentario;

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public Long getOrdenId() { return ordenId; }
    public void setOrdenId(Long ordenId) { this.ordenId = ordenId; }

    public Integer getPuntuacion() { return puntuacion; }
    public void setPuntuacion(Integer puntuacion) { this.puntuacion = puntuacion; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }
}