package cl.duoc.gamehub.shipping.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ShippingRequestDTO {

    @NotNull(message = "El ID de la orden es obligatorio.")
    private Long ordenId;

    @NotNull(message = "El ID del usuario es obligatorio.")
    private Long usuarioId;

    @NotBlank(message = "La direccion de despacho no puede estar vacia.")
    private String direccion;

    @NotBlank(message = "Debe especificar una empresa de transporte.")
    private String transportista;

    public Long getOrdenId() { return ordenId; }
    public void setOrdenId(Long ordenId) { this.ordenId = ordenId; }

    public Long getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Long usuarioId) { this.usuarioId = usuarioId; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public String getTransportista() { return transportista; }
    public void setTransportista(String transportista) { this.transportista = transportista; }
}