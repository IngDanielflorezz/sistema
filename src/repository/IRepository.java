package repository;

import java.util.List;
import java.util.Optional;

public interface IRepository<T> {
    void guardar(T entity);
    void actualizar(T entity);
    void eliminar(String id);
    Optional<T> buscarPorId(String id);
    List<T> buscarTodos();
    void cargarDatos();
    void persistirDatos();
}
