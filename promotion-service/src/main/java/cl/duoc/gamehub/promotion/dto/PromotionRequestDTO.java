package cl.duoc.gamehub.promotion.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class PromotionRequestDTO {

    @NotBlank(message = "El codigo del cupon no puede estar vacio.")
    @Size(min = 3, max = 20, message = "El codigo debe tener entre 3 y 20 caracteres.")
    private String codigo;

    @NotBlank(message = "El tipo de promocion es obligatorio (FIJO o PORCENTAJE).")
    private String tipo;

    @NotNull(message = "El valor del descuento es obligatorio.")
    @DecimalMin(value = "1.0", message = "El valor minimo debe ser 1.")
    private Double valor;

    @NotNull(message = "La fecha de inicio es obligatoria.")
    private LocalDate fechaInicio;

    @NotNull(message = "La fecha de termino es obligatoria.")
    private LocalDate fechaFin;

    @NotNull(message = "El monto minimo de compra es obligatorio.")
    private Double montoMinimo;

    @NotNull(message = "El numero de usos maximos es obligatorio.")
    @Min(value = 1, message = "Los usos maximos deben ser al menos 1.")
    private Integer usosMaximos;

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Double getValor() { return valor; }
    public void setValor(Double valor) { this.valor = valor; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public Double getMontoMinimo() { return montoMinimo; }
    public void setMontoMinimo(Double montoMinimo) { this.montoMinimo = montoMinimo; }

    public Integer getUsosMaximos() { return usosMaximos; }
    public void setUsosMaximos(Integer usosMaximos) { this.usosMaximos = usosMaximos; }
}