package cl.duoc.gamehub.order.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class OrdenDTO {
    @NotNull(message = "El ID del usuario/cliente es obligatorio.")
    private Long usuarioId;
    @NotEmpty(message = "La orden debe contener al menos un producto en el detalle.")
    @Valid
    private List<DetalleOrdenDTO> detalles;
    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }
    public List<DetalleOrdenDTO> getDetalles() { return detalles; }
    public void setDetalles(List<DetalleOrdenDTO> detalles) { this.detalles = detalles; }
}