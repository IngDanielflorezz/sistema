package model;

import java.util.Objects;
import java.util.UUID;

public class Juego {
    private final String id;
    private String titulo;
    private Consola consola;
    private double precio;
    private int stock;

    public Juego(String titulo, Consola consola, double precio, int stock) {
        this.id = UUID.randomUUID().toString();
        this.titulo = titulo;
        this.consola = consola;
        this.precio = precio;
        this.stock = stock;
    }

    public Juego(String id, String titulo, Consola consola, double precio, int stock) {
        this.id = id;
        this.titulo = titulo;
        this.consola = consola;
        this.precio = precio;
        this.stock = stock;
    }

    public String getId() {
        return id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public Consola getConsola() {
        return consola;
    }

    public void setConsola(Consola consola) {
        this.consola = consola;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public boolean hayStockDisponible(int cantidad) {
        return stock >= cantidad && cantidad > 0;
    }

    public void reducirStock(int cantidad) {
        if (hayStockDisponible(cantidad)) {
            stock -= cantidad;
        } else {
            throw new IllegalStateException("Stock insuficiente para " + titulo);
        }
    }

    public void aumentarStock(int cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
        stock += cantidad;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Juego juego = (Juego) o;
        return Objects.equals(id, juego.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("[%s] %-30s | %-16s | $%.2f | Stock: %d",
                id.substring(0, 8), titulo, consola, precio, stock);
    }
}
