package cl.duoc.gamehub.auth.model;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class CuentaAcceso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String rol;
    private String estado;

    public CuentaAcceso() {}

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getRol() { return rol; }
    public String getEstado() { return estado; }
}