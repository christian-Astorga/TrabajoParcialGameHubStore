package cl.duoc.gamehub.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ReservaDTO {

    @NotNull(message = "El ID del producto es obligatorio para la reserva.")
    private Long productoId;

    @NotNull(message = "La cantidad a reservar no puede ser nula.")
    @Min(value = 1, message = "La cantidad mínima a reservar debe ser 1 unidad.")
    private Integer cantidad;

    // Getters y Setters
    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
}