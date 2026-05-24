package cl.duoc.gamehub.inventory.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class InventarioDTO {

    @NotNull(message = "El ID del producto no puede ser nulo.")
    private Long productoId;

    @NotNull(message = "El stock disponible no puede ser nulo.")
    @Min(value = 0, message = "El stock disponible no puede ser negativo.")
    private Integer stockDisponible;

    @NotNull(message = "El stock mínimo no puede ser nulo.")
    @Min(value = 1, message = "El stock mínimo debe ser al menos 1 unidad.")
    private Integer stockMinimo;

    @NotNull(message = "La ubicación en bodega es obligatoria.")
    private String ubicacion;
    // Getters y Setters
    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public Integer getStockDisponible() { return stockDisponible; }
    public void setStockDisponible(Integer stockDisponible) { this.stockDisponible = stockDisponible; }

    public Integer getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
}