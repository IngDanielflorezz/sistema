package repository;

import model.Juego;
import model.Consola;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class JuegoRepository implements IRepository<Juego> {
    private static final String RUTA_ARCHIVO = "data" + File.separator + "juegos.dat";
    private final Map<String, Juego> juegos;

    public JuegoRepository() {
        this.juegos = new HashMap<>();
    }

    @Override
    public void guardar(Juego juego) {
        juegos.put(juego.getId(), juego);
    }

    @Override
    public void actualizar(Juego juego) {
        if (!juegos.containsKey(juego.getId())) {
            throw new IllegalArgumentException("Juego no encontrado: " + juego.getId());
        }
        juegos.put(juego.getId(), juego);
    }

    @Override
    public void eliminar(String id) {
        if (!juegos.containsKey(id)) {
            throw new IllegalArgumentException("Juego no encontrado: " + id);
        }
        juegos.remove(id);
    }

    @Override
    public Optional<Juego> buscarPorId(String id) {
        return Optional.ofNullable(juegos.get(id));
    }

    @Override
    public List<Juego> buscarTodos() {
        return new ArrayList<>(juegos.values());
    }

    public List<Juego> buscarPorConsola(Consola consola) {
        return juegos.values().stream()
                .filter(j -> j.getConsola() == consola)
                .collect(Collectors.toList());
    }

    public List<Juego> buscarPorTitulo(String titulo) {
        String busqueda = titulo.toLowerCase();
        return juegos.values().stream()
                .filter(j -> j.getTitulo().toLowerCase().contains(busqueda))
                .collect(Collectors.toList());
    }

    public boolean existeJuego(String titulo, Consola consola) {
        return juegos.values().stream()
                .anyMatch(j -> j.getTitulo().equalsIgnoreCase(titulo) && j.getConsola() == consola);
    }

    @Override
    public void cargarDatos() {
        Path ruta = Paths.get(RUTA_ARCHIVO);
        if (!Files.exists(ruta)) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(RUTA_ARCHIVO))) {
            String linea;
            while ((linea = reader.readLine()) != null) {
                String[] partes = linea.split("\\|");
                if (partes.length == 5) {
                    String id = partes[0].trim();
                    String titulo = partes[1].trim();
                    Consola consola = Consola.valueOf(partes[2].trim());
                    double precio = Double.parseDouble(partes[3].trim());
                    int stock = Integer.parseInt(partes[4].trim());
                    juegos.put(id, new Juego(id, titulo, consola, precio, stock));
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Error al cargar juegos: " + e.getMessage());
        }
    }

    @Override
    public void persistirDatos() {
        try {
            Files.createDirectories(Paths.get("data"));
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(RUTA_ARCHIVO))) {
                for (Juego juego : juegos.values()) {
                    String linea = String.format("%s|%s|%s|%.2f|%d",
                            juego.getId(),
                            juego.getTitulo(),
                            juego.getConsola().name(),
                            juego.getPrecio(),
                            juego.getStock());
                    writer.write(linea);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Error al persistir juegos: " + e.getMessage());
        }
    }
}
