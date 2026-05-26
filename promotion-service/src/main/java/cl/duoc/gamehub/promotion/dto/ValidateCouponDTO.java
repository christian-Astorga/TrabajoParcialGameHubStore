package cl.duoc.gamehub.promotion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ValidateCouponDTO {

    @NotBlank(message = "El codigo del cupon es obligatorio.")
    private String codigo;

    @NotNull(message = "El monto total de la orden es obligatorio.")
    private Double totalOrden;

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public Double getTotalOrden() { return totalOrden; }
    public void setTotalOrden(Double totalOrden) { this.totalOrden = totalOrden; }
}