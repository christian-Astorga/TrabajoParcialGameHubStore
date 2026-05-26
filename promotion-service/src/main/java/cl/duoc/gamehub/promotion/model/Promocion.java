package cl.duoc.gamehub.promotion.model;

import jakarta.persistence.*;
import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDate;

@Entity
@Table(name = "promociones")
public class Promocion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String codigo;

    private String tipo;
    private Double valor;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "fecha_inicio", columnDefinition = "DATE")
    private LocalDate fechaInicio;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @Column(name = "fecha_fin", columnDefinition = "DATE")
    private LocalDate fechaFin;

    @Column(name = "monto_minimo")
    private Double montoMinimo;

    @Column(name = "usos_maximos")
    private Integer usosMaximos;

    @Column(name = "usos_actuales")
    private Integer usosActuales;

    private String estado;

    public Promocion() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public Integer getUsosActuales() { return usosActuales; }
    public void setUsosActuales(Integer usosActuales) { this.usosActuales = usosActuales; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}