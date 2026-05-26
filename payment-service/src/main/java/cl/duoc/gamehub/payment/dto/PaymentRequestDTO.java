package cl.duoc.gamehub.payment.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PaymentRequestDTO {

    @NotNull(message = "El ID de la orden es obligatorio.")
    private Long ordenId;

    @NotNull(message = "El monto es obligatorio.")
    @Min(value = 1, message = "El monto debe ser mayor a cero.")
    private Double monto;

    @NotBlank(message = "El metodo de pago es obligatorio.")
    private String metodo;

    public PaymentRequestDTO() {}

    public Long getOrdenId() { return ordenId; }
    public void setOrdenId(Long ordenId) { this.ordenId = ordenId; }

    public Double getMonto() { return monto; }
    public void setMonto(Double monto) { this.monto = monto; }

    public String getMetodo() { return metodo; }
    public void setMetodo(String metodo) { this.metodo = metodo; }
}