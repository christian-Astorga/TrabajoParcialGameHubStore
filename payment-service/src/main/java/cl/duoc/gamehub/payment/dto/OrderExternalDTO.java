package cl.duoc.gamehub.payment.dto;

public class OrderExternalDTO {
    private Long id;
    private Double total;
    private String estado;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Double getTotal() { return total; }
    public void setTotal(Double total) { this.total = total; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}