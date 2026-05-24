package cl.duoc.gamehub.inventory.model;

import jakarta.persistence.*;

@Entity
@Table(name = "inventarios")
public class Inventario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "producto_id", nullable = false, unique = true)
    private Long productoId;

    @Column(name = "stock_disponible", nullable = false)
    private Integer stockDisponible;

    @Column(name = "stock_reservado", nullable = false)
    private Integer stockReservado;

    @Column(name = "stock_minimo", nullable = false)
    private Integer stockMinimo;

    @Column(nullable = false)
    private String ubicacion;

    // Constructores
    public Inventario() {}

    public Inventario(Long productoId, Integer stockDisponible, Integer stockReservado, Integer stockMinimo, String ubicacion) {
        this.productoId = productoId;
        this.stockDisponible = stockDisponible;
        this.stockReservado = stockReservado;
        this.stockMinimo = stockMinimo;
        this.ubicacion = ubicacion;
    }

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }

    public Integer getStockDisponible() { return stockDisponible; }
    public void setStockDisponible(Integer stockDisponible) { this.stockDisponible = stockDisponible; }

    public Integer getStockReservado() { return stockReservado; }
    public void setStockReservado(Integer stockReservado) { this.stockReservado = stockReservado; }

    public Integer getStockMinimo() { return stockMinimo; }
    public void setStockMinimo(Integer stockMinimo) { this.stockMinimo = stockMinimo; }

    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
}