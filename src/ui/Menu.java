package ui;

import model.*;
import repository.*;
import service.*;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class Menu {
    private final Scanner scanner;
    private final TiendaService tiendaService;
    private final InventarioService inventarioService;

    public Menu(TiendaService tiendaService, InventarioService inventarioService) {
        this.scanner = new Scanner(System.in);
        this.tiendaService = tiendaService;
        this.inventarioService = inventarioService;
    }

    public void iniciar() {
        boolean continuar = true;
        while (continuar) {
            mostrarMenuPrincipal();
            int opcion = leerEntero("Seleccione una opcion: ");

            switch (opcion) {
                case 1 -> menuInventario();
                case 2 -> menuClientes();
                case 3 -> menuVentas();
                case 4 -> reportes();
                case 5 -> continuar = false;
                default -> System.out.println("Opcion no valida.");
            }
        }
        System.out.println("Gracias por usar GameStore. Hasta luego!");
    }

    private void mostrarMenuPrincipal() {
        System.out.println("\n========================================");
        System.out.println("        GAME STORE - Menu Principal");
        System.out.println("========================================");
        System.out.println("1. Gestion de Inventario");
        System.out.println("2. Gestion de Clientes");
        System.out.println("3. Gestion de Ventas");
        System.out.println("4. Reportes");
        System.out.println("5. Salir");
        System.out.println("========================================");
    }

    private void menuInventario() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- Gestion de Inventario ---");
            System.out.println("1. Registrar juego");
            System.out.println("2. Listar juegos");
            System.out.println("3. Buscar por titulo");
            System.out.println("4. Buscar por consola");
            System.out.println("5. Actualizar juego");
            System.out.println("6. Eliminar juego");
            System.out.println("7. Reabastecer stock");
            System.out.println("8. Volver");

            int opcion = leerEntero("Seleccione: ");
            switch (opcion) {
                case 1 -> registrarJuego();
                case 2 -> listarJuegos();
                case 3 -> buscarJuegoPorTitulo();
                case 4 -> buscarJuegoPorConsola();
                case 5 -> actualizarJuego();
                case 6 -> eliminarJuego();
                case 7 -> reabastecerJuego();
                case 8 -> volver = true;
                default -> System.out.println("Opcion no valida.");
            }
        }
    }

    private void registrarJuego() {
        System.out.println("\n--- Registrar Nuevo Juego ---");
        String titulo = leerTexto("Titulo del juego: ");
        Consola consola = seleccionarConsola();
        double precio = leerDouble("Precio: ");
        int stock = leerEntero("Stock inicial: ");

        try {
            Juego juego = inventarioService.registrarJuego(titulo, consola, precio, stock);
            System.out.println("Juego registrado exitosamente! ID: " + juego.getId().substring(0, 8));
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listarJuegos() {
        List<Juego> juegos = inventarioService.listarJuegos();
        if (juegos.isEmpty()) {
            System.out.println("No hay juegos registrados.");
            return;
        }
        System.out.println("\n--- Lista de Juegos ---");
        juegos.forEach(System.out::println);
    }

    private void buscarJuegoPorTitulo() {
        String titulo = leerTexto("Ingrese titulo a buscar: ");
        List<Juego> resultados = inventarioService.buscarPorTitulo(titulo);
        if (resultados.isEmpty()) {
            System.out.println("No se encontraron juegos con ese titulo.");
            return;
        }
        resultados.forEach(System.out::println);
    }

    private void buscarJuegoPorConsola() {
        Consola consola = seleccionarConsola();
        List<Juego> resultados = inventarioService.buscarPorConsola(consola);
        if (resultados.isEmpty()) {
            System.out.println("No hay juegos para esa consola.");
            return;
        }
        resultados.forEach(System.out::println);
    }

    private void actualizarJuego() {
        String id = leerTexto("ID del juego: ");
        Juego juego = inventarioService.obtenerJuego(id).orElse(null);
        if (juego == null) {
            System.out.println("Juego no encontrado.");
            return;
        }

        System.out.println("Juego actual: " + juego.getTitulo());
        String titulo = leerTexto("Nuevo titulo (Enter para mantener): ");
        if (titulo.isEmpty()) titulo = juego.getTitulo();

        Consola consola = seleccionarConsola();
        double precio = leerDouble("Nuevo precio: ");
        int stock = leerEntero("Nuevo stock: ");

        try {
            inventarioService.actualizarJuego(id, titulo, consola, precio, stock);
            System.out.println("Juego actualizado exitosamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void eliminarJuego() {
        String id = leerTexto("ID del juego a eliminar: ");
        try {
            inventarioService.eliminarJuego(id);
            System.out.println("Juego eliminado.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void reabastecerJuego() {
        String id = leerTexto("ID del juego: ");
        int cantidad = leerEntero("Cantidad a agregar: ");
        try {
            inventarioService.reabastecerJuego(id, cantidad);
            System.out.println("Stock actualizado.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void menuClientes() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- Gestion de Clientes ---");
            System.out.println("1. Registrar cliente");
            System.out.println("2. Listar clientes");
            System.out.println("3. Actualizar cliente");
            System.out.println("4. Eliminar cliente");
            System.out.println("5. Volver");

            int opcion = leerEntero("Seleccione: ");
            switch (opcion) {
                case 1 -> registrarCliente();
                case 2 -> listarClientes();
                case 3 -> actualizarCliente();
                case 4 -> eliminarCliente();
                case 5 -> volver = true;
                default -> System.out.println("Opcion no valida.");
            }
        }
    }

    private void registrarCliente() {
        System.out.println("\n--- Registrar Nuevo Cliente ---");
        String nombre = leerTexto("Nombre: ");
        String email = leerTexto("Email: ");
        String telefono = leerTexto("Telefono: ");

        try {
            Cliente cliente = tiendaService.registrarCliente(nombre, email, telefono);
            System.out.println("Cliente registrado exitosamente! ID: " + cliente.getId().substring(0, 8));
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listarClientes() {
        List<Cliente> clientes = tiendaService.listarClientes();
        if (clientes.isEmpty()) {
            System.out.println("No hay clientes registrados.");
            return;
        }
        System.out.println("\n--- Lista de Clientes ---");
        clientes.forEach(System.out::println);
    }

    private void actualizarCliente() {
        String id = leerTexto("ID del cliente: ");
        Cliente cliente = tiendaService.obtenerCliente(id).orElse(null);
        if (cliente == null) {
            System.out.println("Cliente no encontrado.");
            return;
        }

        System.out.println("Cliente actual: " + cliente.getNombre());
        String nombre = leerTexto("Nuevo nombre (Enter para mantener): ");
        if (nombre.isEmpty()) nombre = cliente.getNombre();

        String email = leerTexto("Nuevo email (Enter para mantener): ");
        if (email.isEmpty()) email = cliente.getEmail();

        String telefono = leerTexto("Nuevo telefono (Enter para mantener): ");
        if (telefono.isEmpty()) telefono = cliente.getTelefono();

        try {
            tiendaService.actualizarCliente(id, nombre, email, telefono);
            System.out.println("Cliente actualizado exitosamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void eliminarCliente() {
        String id = leerTexto("ID del cliente a eliminar: ");
        try {
            tiendaService.eliminarCliente(id);
            System.out.println("Cliente eliminado.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void menuVentas() {
        boolean volver = false;
        while (!volver) {
            System.out.println("\n--- Gestion de Ventas ---");
            System.out.println("1. Crear nueva venta");
            System.out.println("2. Listar ventas");
            System.out.println("3. Ver detalle de venta");
            System.out.println("4. Aplicar descuento a venta");
            System.out.println("5. Volver");

            int opcion = leerEntero("Seleccione: ");
            switch (opcion) {
                case 1 -> crearVenta();
                case 2 -> listarVentas();
                case 3 -> verDetalleVenta();
                case 4 -> aplicarDescuento();
                case 5 -> volver = true;
                default -> System.out.println("Opcion no valida.");
            }
        }
    }

    private void crearVenta() {
        System.out.println("\n--- Nueva Venta ---");
        listarClientes();
        String clienteId = leerTexto("ID del cliente: ");

        try {
            Venta venta = tiendaService.crearVenta(clienteId);
            System.out.println("Venta creada para: " + venta.getCliente().getNombre());

            boolean agregarMas = true;
            while (agregarMas) {
                listarJuegos();
                String juegoId = leerTexto("ID del juego: ");
                int cantidad = leerEntero("Cantidad: ");

                try {
                    tiendaService.agregarItemAVenta(venta.getId(), juegoId, cantidad);
                    System.out.println("Item agregado. Total actual: $" + String.format("%.2f", venta.calcularTotal()));
                } catch (IllegalArgumentException | IllegalStateException e) {
                    System.out.println("Error: " + e.getMessage());
                }

                System.out.print("¿Agregar otro item? (s/n): ");
                agregarMas = scanner.nextLine().trim().equalsIgnoreCase("s");
            }

            tiendaService.completarVenta(venta.getId());
            System.out.println("\nVenta completada exitosamente!");
            System.out.println("Total: $" + String.format("%.2f", venta.calcularTotal()));
            System.out.println("Items: " + venta.calcularCantidadItems());
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void listarVentas() {
        List<Venta> ventas = tiendaService.listarVentas();
        if (ventas.isEmpty()) {
            System.out.println("No hay ventas registradas.");
            return;
        }
        System.out.println("\n--- Lista de Ventas ---");
        ventas.forEach(System.out::println);
    }

    private void verDetalleVenta() {
        String id = leerTexto("ID de la venta: ");
        Venta venta = tiendaService.obtenerVenta(id).orElse(null);
        if (venta == null) {
            System.out.println("Venta no encontrada.");
            return;
        }

        System.out.println("\n========================================");
        System.out.println("  DETALLE DE VENTA");
        System.out.println("========================================");
        System.out.println("Venta ID: " + venta.getId().substring(0, 8));
        System.out.println("Cliente: " + venta.getCliente().getNombre());
        System.out.println("Fecha: " + venta.getFecha().toString().substring(0, 16));
        System.out.println("----------------------------------------");
        venta.getDetalles().forEach(System.out::println);
        System.out.println("----------------------------------------");
        System.out.println("TOTAL: $" + String.format("%.2f", venta.calcularTotal()));
        System.out.println("========================================");
    }

    private void aplicarDescuento() {
        String id = leerTexto("ID de la venta: ");
        double porcentaje = leerDouble("Porcentaje de descuento (0-100): ");

        try {
            tiendaService.aplicarDescuentoAVenta(id, porcentaje);
            System.out.println("Descuento aplicado exitosamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private void reportes() {
        System.out.println("\n--- Reportes ---");
        System.out.println("1. Valor total del inventario");
        System.out.println("2. Juegos sin stock");
        System.out.println("3. Total vendido");
        System.out.println("4. Estadisticas por consola");

        int opcion = leerEntero("Seleccione: ");
        switch (opcion) {
            case 1 -> {
                double valor = inventarioService.calcularValorTotalInventario();
                System.out.println("Valor total del inventario: $" + String.format("%.2f", valor));
            }
            case 2 -> {
                List<Juego> sinStock = inventarioService.obtenerJuegosSinStock();
                if (sinStock.isEmpty()) {
                    System.out.println("Todos los juegos tienen stock.");
                } else {
                    System.out.println("\nJuegos sin stock:");
                    sinStock.forEach(System.out::println);
                }
            }
            case 3 -> {
                double total = tiendaService.calcularTotalVentas();
                System.out.println("Total vendido: $" + String.format("%.2f", total));
            }
            case 4 -> {
                System.out.println("\nEstadisticas por consola:");
                for (Consola consola : Consola.values()) {
                    List<Juego> juegos = inventarioService.buscarPorConsola(consola);
                    if (!juegos.isEmpty()) {
                        int totalStock = juegos.stream().mapToInt(Juego::getStock).sum();
                        System.out.printf("  %-16s: %d juegos, %d unidades%n", consola, juegos.size(), totalStock);
                    }
                }
            }
            default -> System.out.println("Opcion no valida.");
        }
    }

    private Consola seleccionarConsola() {
        System.out.println("Seleccione consola:");
        Consola[] consolas = Consola.values();
        for (int i = 0; i < consolas.length; i++) {
            System.out.printf("  %d. %s%n", i + 1, consolas[i]);
        }
        int opcion = leerEntero("Opcion: ");
        if (opcion < 1 || opcion > consolas.length) {
            System.out.println("Opcion no valida, usando PLAYSTATION_5 por defecto.");
            return Consola.PLAYSTATION_5;
        }
        return consolas[opcion - 1];
    }

    private String leerTexto(String mensaje) {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }

    private int leerEntero(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un numero valido.");
            }
        }
    }

    private double leerDouble(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Ingrese un numero valido.");
            }
        }
    }
}
