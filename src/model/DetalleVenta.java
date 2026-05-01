package model;

import java.util.Objects;

public class DetalleVenta {
    private final Juego juego;
    private int cantidad;
    private double descuento;

    public DetalleVenta(Juego juego, int cantidad) {
        this.juego = Objects.requireNonNull(juego, "El juego no puede ser nulo");
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
        this.cantidad = cantidad;
        this.descuento = 0;
    }

    public Juego getJuego() {
        return juego;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void aumentarCantidad(int cantidadAdicional) {
        if (cantidadAdicional <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
        if (!juego.hayStockDisponible(cantidadAdicional)) {
            throw new IllegalStateException("Stock insuficiente para " + juego.getTitulo());
        }
        this.cantidad += cantidadAdicional;
    }

    public double getDescuento() {
        return descuento;
    }

    public void aplicarDescuento(double porcentaje) {
        if (porcentaje < 0 || porcentaje > 100) {
            throw new IllegalArgumentException("Porcentaje debe estar entre 0 y 100");
        }
        this.descuento = porcentaje;
    }

    public double getPrecioUnitario() {
        return juego.getPrecio();
    }

    public double getSubtotal() {
        double totalBruto = getPrecioUnitario() * cantidad;
        return totalBruto * (1 - descuento / 100);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DetalleVenta that = (DetalleVenta) o;
        return Objects.equals(juego.getId(), that.juego.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(juego.getId());
    }

    @Override
    public String toString() {
        return String.format("  %-30s x%d | $%.2f c/u | Desc: %.0f%% | Subtotal: $%.2f",
                juego.getTitulo(), cantidad, getPrecioUnitario(), descuento, getSubtotal());
    }
}
