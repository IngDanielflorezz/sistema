package repository;

import model.Venta;
import model.DetalleVenta;
import model.Juego;
import model.Cliente;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class VentaRepository implements IRepository<Venta> {
    private static final String RUTA_ARCHIVO = "data" + File.separator + "ventas.dat";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private final Map<String, Venta> ventas;
    private final JuegoRepository juegoRepository;
    private final ClienteRepository clienteRepository;

    public VentaRepository(JuegoRepository juegoRepository, ClienteRepository clienteRepository) {
        this.ventas = new HashMap<>();
        this.juegoRepository = juegoRepository;
        this.clienteRepository = clienteRepository;
    }

    @Override
    public void guardar(Venta venta) {
        ventas.put(venta.getId(), venta);
    }

    @Override
    public void actualizar(Venta venta) {
        if (!ventas.containsKey(venta.getId())) {
            throw new IllegalArgumentException("Venta no encontrada: " + venta.getId());
        }
        ventas.put(venta.getId(), venta);
    }

    @Override
    public void eliminar(String id) {
        if (!ventas.containsKey(id)) {
            throw new IllegalArgumentException("Venta no encontrada: " + id);
        }
        ventas.remove(id);
    }

    @Override
    public Optional<Venta> buscarPorId(String id) {
        return Optional.ofNullable(ventas.get(id));
    }

    @Override
    public List<Venta> buscarTodos() {
        return new ArrayList<>(ventas.values());
    }

    public List<Venta> buscarPorCliente(String clienteId) {
        return ventas.values().stream()
                .filter(v -> v.getCliente().getId().equals(clienteId))
                .toList();
    }

    @Override
    public void cargarDatos() {
        Path ruta = Paths.get(RUTA_ARCHIVO);
        if (!Files.exists(ruta)) {
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(RUTA_ARCHIVO))) {
            String linea;
            List<DetalleVenta> detallesActuales = new ArrayList<>();
            Venta ventaActual = null;

            while ((linea = reader.readLine()) != null) {
                if (linea.startsWith("VENTA:")) {
                    if (ventaActual != null && !detallesActuales.isEmpty()) {
                        ventas.put(ventaActual.getId(), ventaActual);
                    }
                    String[] partes = linea.substring(6).split("\\|");
                    if (partes.length == 3) {
                        String ventaId = partes[0].trim();
                        String clienteId = partes[1].trim();
                        LocalDateTime fecha = LocalDateTime.parse(partes[2].trim(), FORMATTER);

                        Cliente cliente = clienteRepository.buscarPorId(clienteId).orElse(null);
                        if (cliente != null) {
                            ventaActual = new Venta(ventaId, cliente, fecha, new ArrayList<>());
                            detallesActuales = new ArrayList<>();
                        }
                    }
                } else if (linea.startsWith("DETALLE:") && ventaActual != null) {
                    String[] partes = linea.substring(8).split("\\|");
                    if (partes.length == 4) {
                        String juegoId = partes[0].trim();
                        int cantidad = Integer.parseInt(partes[1].trim());
                        double descuento = Double.parseDouble(partes[2].trim());

                        Juego juego = juegoRepository.buscarPorId(juegoId).orElse(null);
                        if (juego != null) {
                            DetalleVenta detalle = new DetalleVenta(juego, cantidad);
                            if (descuento > 0) {
                                detalle.aplicarDescuento(descuento);
                            }
                            detallesActuales.add(detalle);
                        }
                    }
                }
            }

            if (ventaActual != null && !detallesActuales.isEmpty()) {
                ventas.put(ventaActual.getId(), ventaActual);
            }
        } catch (IOException | IllegalArgumentException e) {
            System.err.println("Error al cargar ventas: " + e.getMessage());
        }
    }

    @Override
    public void persistirDatos() {
        try {
            Files.createDirectories(Paths.get("data"));
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(RUTA_ARCHIVO))) {
                for (Venta venta : ventas.values()) {
                    String ventaLinea = String.format("VENTA:%s|%s|%s",
                            venta.getId(),
                            venta.getCliente().getId(),
                            venta.getFecha().format(FORMATTER));
                    writer.write(ventaLinea);
                    writer.newLine();

                    for (DetalleVenta detalle : venta.getDetalles()) {
                        String detalleLinea = String.format("DETALLE:%s|%d|%.2f|%.2f",
                                detalle.getJuego().getId(),
                                detalle.getCantidad(),
                                detalle.getDescuento(),
                                detalle.getSubtotal());
                        writer.write(detalleLinea);
                        writer.newLine();
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al persistir ventas: " + e.getMessage());
        }
    }
}
