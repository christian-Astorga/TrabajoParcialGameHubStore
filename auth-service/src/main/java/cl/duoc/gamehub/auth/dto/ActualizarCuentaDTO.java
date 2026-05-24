package cl.duoc.gamehub.auth.dto;

public class ActualizarCuentaDTO {
    private String password; // Opcional, si viene se cambia
    private String rol;
    private String estado;

    public ActualizarCuentaDTO() {}

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}