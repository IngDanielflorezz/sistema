package service;

import model.Juego;
import model.Consola;
import repository.JuegoRepository;

import java.util.List;
import java.util.Optional;

public class InventarioService {
    private final JuegoRepository juegoRepository;

    public InventarioService(JuegoRepository juegoRepository) {
        this.juegoRepository = juegoRepository;
    }

    public Juego registrarJuego(String titulo, Consola consola, double precio, int stock) {
        validarDatosJuego(titulo, consola, precio, stock);

        if (juegoRepository.existeJuego(titulo, consola)) {
            throw new IllegalArgumentException("Ya existe un juego con ese titulo para la consola seleccionada");
        }

        Juego juego = new Juego(titulo, consola, precio, stock);
        juegoRepository.guardar(juego);
        return juego;
    }

    public Optional<Juego> obtenerJuego(String id) {
        return juegoRepository.buscarPorId(id);
    }

    public List<Juego> listarJuegos() {
        return juegoRepository.buscarTodos();
    }

    public List<Juego> buscarPorConsola(Consola consola) {
        return juegoRepository.buscarPorConsola(consola);
    }

    public List<Juego> buscarPorTitulo(String titulo) {
        return juegoRepository.buscarPorTitulo(titulo);
    }

    public Juego actualizarJuego(String id, String nuevoTitulo, Consola nuevaConsola, double nuevoPrecio, int nuevoStock) {
        Juego juego = juegoRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Juego no encontrado"));

        validarDatosJuego(nuevoTitulo, nuevaConsola, nuevoPrecio, nuevoStock);
        juego.setTitulo(nuevoTitulo);
        juego.setConsola(nuevaConsola);
        juego.setPrecio(nuevoPrecio);
        juego.setStock(nuevoStock);

        juegoRepository.actualizar(juego);
        return juego;
    }

    public void eliminarJuego(String id) {
        juegoRepository.eliminar(id);
    }

    public void reabastecerJuego(String id, int cantidad) {
        Juego juego = juegoRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Juego no encontrado"));
        juego.aumentarStock(cantidad);
        juegoRepository.actualizar(juego);
    }

    public double calcularValorTotalInventario() {
        return juegoRepository.buscarTodos().stream()
                .mapToDouble(j -> j.getPrecio() * j.getStock())
                .sum();
    }

    public List<Juego> obtenerJuegosSinStock() {
        return juegoRepository.buscarTodos().stream()
                .filter(j -> j.getStock() == 0)
                .toList();
    }

    private void validarDatosJuego(String titulo, Consola consola, double precio, int stock) {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("El titulo no puede estar vacio");
        }
        if (consola == null) {
            throw new IllegalArgumentException("La consola es requerida");
        }
        if (precio < 0) {
            throw new IllegalArgumentException("El precio no puede ser negativo");
        }
        if (stock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
    }
}
