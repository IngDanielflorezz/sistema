package repository;

import model.Cliente;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class ClienteRepository implements IRepository<Cliente> {
    private static final String RUTA_ARCHIVO = "data" + File.separator + "clientes.dat";
    private final Map<String, Cliente> clientes;

    public ClienteRepository() {
        this.clientes = new HashMap<>();
    }

    @Override
    public void guardar(Cliente cliente) {
        clientes.put(cliente.getId(), cliente);
    }

    @Override
    public void actualizar(Cliente cliente) {
        if (!clientes.containsKey(cliente.getId())) {
            throw new IllegalArgumentException("Cliente no encontrado: " + cliente.getId());
        }
        clientes.put(cliente.getId(), cliente);
    }

    @Override
    public void eliminar(String id) {
        if (!clientes.containsKey(id)) {
            throw new IllegalArgumentException("Cliente no encontrado: " + id);
        }
        clientes.remove(id);
    }

    @Override
    public Optional<Cliente> buscarPorId(String id) {
        return Optional.ofNullable(clientes.get(id));
    }

    @Override
    public List<Cliente> buscarTodos() {
        return new ArrayList<>(clientes.values());
    }

    public Optional<Cliente> buscarPorEmail(String email) {
        return clientes.values().stream()
                .filter(c -> c.getEmail().equalsIgnoreCase(email))
                .findFirst();
    }

    public List<Cliente> buscarPorNombre(String nombre) {
        String busqueda = nombre.toLowerCase();
        return clientes.values().stream()
                .filter(c -> c.getNombre().toLowerCase().contains(busqueda))
                .toList();
    }

    public boolean existeEmail(String email) {
        return clientes.values().stream()
                .anyMatch(c -> c.getEmail().equalsIgnoreCase(email));
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
                if (partes.length == 4) {
                    String id = partes[0].trim();
                    String nombre = partes[1].trim();
                    String email = partes[2].trim();
                    String telefono = partes[3].trim();
                    clientes.put(id, new Cliente(id, nombre, email, telefono));
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Error al cargar clientes: " + e.getMessage());
        }
    }

    @Override
    public void persistirDatos() {
        try {
            Files.createDirectories(Paths.get("data"));
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(RUTA_ARCHIVO))) {
                for (Cliente cliente : clientes.values()) {
                    String linea = String.format("%s|%s|%s|%s",
                            cliente.getId(),
                            cliente.getNombre(),
                            cliente.getEmail(),
                            cliente.getTelefono());
                    writer.write(linea);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Error al persistir clientes: " + e.getMessage());
        }
    }
}
