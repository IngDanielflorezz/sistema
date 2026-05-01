import model.Juego;
import model.Consola;
import repository.*;
import service.*;
import ui.Menu;

public class Main {
    public static void main(String[] args) {
        JuegoRepository juegoRepo = new JuegoRepository();
        ClienteRepository clienteRepo = new ClienteRepository();
        VentaRepository ventaRepo = new VentaRepository(juegoRepo, clienteRepo);

        juegoRepo.cargarDatos();
        clienteRepo.cargarDatos();
        ventaRepo.cargarDatos();

        InventarioService inventarioService = new InventarioService(juegoRepo);
        TiendaService tiendaService = new TiendaService(clienteRepo, juegoRepo, ventaRepo);

        Menu menu = new Menu(tiendaService, inventarioService);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            juegoRepo.persistirDatos();
            clienteRepo.persistirDatos();
            ventaRepo.persistirDatos();
        }));

        menu.iniciar();
    }
}
