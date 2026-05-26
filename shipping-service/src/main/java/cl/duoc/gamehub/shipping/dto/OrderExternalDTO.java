package cl.duoc.gamehub.shipping.dto;

public class OrderExternalDTO {
    private Long id;
    private String estado;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}