package service;

import model.Cliente;
import model.Juego;
import model.Venta;
import repository.ClienteRepository;
import repository.VentaRepository;
import repository.JuegoRepository;

import java.util.List;
import java.util.Optional;

public class TiendaService {
    private final ClienteRepository clienteRepository;
    private final JuegoRepository juegoRepository;
    private final VentaRepository ventaRepository;

    public TiendaService(ClienteRepository clienteRepository, JuegoRepository juegoRepository, VentaRepository ventaRepository) {
        this.clienteRepository = clienteRepository;
        this.juegoRepository = juegoRepository;
        this.ventaRepository = ventaRepository;
    }

    public Cliente registrarCliente(String nombre, String email, String telefono) {
        validarDatosCliente(nombre, email, telefono);

        if (clienteRepository.existeEmail(email)) {
            throw new IllegalArgumentException("Ya existe un cliente con ese email");
        }

        Cliente cliente = new Cliente(nombre, email, telefono);
        clienteRepository.guardar(cliente);
        return cliente;
    }

    public Optional<Cliente> obtenerCliente(String id) {
        return clienteRepository.buscarPorId(id);
    }

    public List<Cliente> listarClientes() {
        return clienteRepository.buscarTodos();
    }

    public Cliente actualizarCliente(String id, String nuevoNombre, String nuevoEmail, String nuevoTelefono) {
        Cliente cliente = clienteRepository.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));

        validarDatosCliente(nuevoNombre, nuevoEmail, nuevoTelefono);

        if (!cliente.getEmail().equalsIgnoreCase(nuevoEmail) && clienteRepository.existeEmail(nuevoEmail)) {
            throw new IllegalArgumentException("Ya existe un cliente con ese email");
        }

        cliente.setNombre(nuevoNombre);
        cliente.setEmail(nuevoEmail);
        cliente.setTelefono(nuevoTelefono);

        clienteRepository.actualizar(cliente);
        return cliente;
    }

    public void eliminarCliente(String id) {
        clienteRepository.eliminar(id);
    }

    public Venta crearVenta(String clienteId) {
        Cliente cliente = clienteRepository.buscarPorId(clienteId)
                .orElseThrow(() -> new IllegalArgumentException("Cliente no encontrado"));
        return new Venta(cliente);
    }

    public Venta agregarItemAVenta(String ventaId, String juegoId, int cantidad) {
        Venta venta = ventaRepository.buscarPorId(ventaId)
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada"));

        Juego juego = juegoRepository.buscarPorId(juegoId)
                .orElseThrow(() -> new IllegalArgumentException("Juego no encontrado"));

        venta.agregarDetalle(juego, cantidad);
        ventaRepository.actualizar(venta);
        return venta;
    }

    public Venta completarVenta(String ventaId) {
        Venta venta = ventaRepository.buscarPorId(ventaId)
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada"));

        if (venta.getDetalles().isEmpty()) {
            throw new IllegalStateException("La venta no tiene items");
        }

        venta.procesarVenta();
        ventaRepository.guardar(venta);
        return venta;
    }

    public Venta aplicarDescuentoAVenta(String ventaId, double porcentaje) {
        Venta venta = ventaRepository.buscarPorId(ventaId)
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada"));
        venta.aplicarDescuento(porcentaje);
        ventaRepository.actualizar(venta);
        return venta;
    }

    public Optional<Venta> obtenerVenta(String id) {
        return ventaRepository.buscarPorId(id);
    }

    public List<Venta> listarVentas() {
        return ventaRepository.buscarTodos();
    }

    public List<Venta> obtenerVentasPorCliente(String clienteId) {
        return ventaRepository.buscarPorCliente(clienteId);
    }

    public double calcularTotalVentas() {
        return ventaRepository.buscarTodos().stream()
                .mapToDouble(Venta::calcularTotal)
                .sum();
    }

    private void validarDatosCliente(String nombre, String email, String telefono) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede estar vacio");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new IllegalArgumentException("El email no puede estar vacio");
        }
        if (!email.matches("^[\\w-.]+@[\\w-]+\\.[a-z]{2,}$")) {
            throw new IllegalArgumentException("El email no tiene un formato valido");
        }
        if (telefono == null || telefono.trim().isEmpty()) {
            throw new IllegalArgumentException("El telefono no puede estar vacio");
        }
    }
}
