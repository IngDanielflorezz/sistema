package model;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.ArrayList;

public class Venta {
    private final String id;
    private final Cliente cliente;
    private final LocalDateTime fecha;
    private final List<DetalleVenta> detalles;

    public Venta(Cliente cliente) {
        this.id = UUID.randomUUID().toString();
        this.cliente = cliente;
        this.fecha = LocalDateTime.now();
        this.detalles = new ArrayList<>();
    }

    public Venta(String id, Cliente cliente, LocalDateTime fecha, List<DetalleVenta> detalles) {
        this.id = id;
        this.cliente = cliente;
        this.fecha = fecha;
        this.detalles = new ArrayList<>(detalles);
    }

    public String getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public List<DetalleVenta> getDetalles() {
        return Collections.unmodifiableList(detalles);
    }

    public void agregarDetalle(Juego juego, int cantidad) {
        if (juego == null) {
            throw new IllegalArgumentException("El juego no puede ser nulo");
        }
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
        if (!juego.hayStockDisponible(cantidad)) {
            throw new IllegalStateException("Stock insuficiente para " + juego.getTitulo());
        }

        for (DetalleVenta detalle : detalles) {
            if (detalle.getJuego().getId().equals(juego.getId())) {
                detalle.aumentarCantidad(cantidad);
                return;
            }
        }
        detalles.add(new DetalleVenta(juego, cantidad));
    }

    public double calcularTotal() {
        return detalles.stream()
                .mapToDouble(DetalleVenta::getSubtotal)
                .sum();
    }

    public int calcularCantidadItems() {
        return detalles.stream()
                .mapToInt(DetalleVenta::getCantidad)
                .sum();
    }

    public void aplicarDescuento(double porcentaje) {
        if (porcentaje < 0 || porcentaje > 100) {
            throw new IllegalArgumentException("Porcentaje debe estar entre 0 y 100");
        }
        for (DetalleVenta detalle : detalles) {
            detalle.aplicarDescuento(porcentaje);
        }
    }

    public void procesarVenta() {
        for (DetalleVenta detalle : detalles) {
            detalle.getJuego().reducirStock(detalle.getCantidad());
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Venta venta = (Venta) o;
        return Objects.equals(id, venta.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Venta[%s] - %s - %s - Total: $%.2f",
                id.substring(0, 8), cliente.getNombre(), fecha.toString().substring(0, 16), calcularTotal());
    }
}
