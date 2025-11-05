package ar.edu.utn.frba.dds.coleccion.repositorio;

import ar.edu.utn.frba.dds.coleccion.Coleccion;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import java.util.List;

public class RepositorioColeccionesJPA implements RepositorioColecciones, WithSimplePersistenceUnit {

  @Override
  public void guardar(Coleccion coleccion) {
    withTransaction(() -> entityManager().persist(coleccion));
  }

  @Override
  public Coleccion buscarPorId(Long id) {
    return withTransaction(() -> entityManager().find(Coleccion.class, id));
  }

  @Override
  public List<Coleccion> buscarTodas() {
    return withTransaction(() ->
        entityManager()
            .createQuery("SELECT c FROM Coleccion c", Coleccion.class)
            .getResultList()
    );
  }

  @Override
  public Coleccion actualizar(Coleccion coleccion) {
    return withTransaction(() -> entityManager().merge(coleccion));
  }

  @Override
  public void eliminar(Coleccion coleccion) {
    withTransaction(() -> {
      Coleccion c = entityManager().merge(coleccion);
      entityManager().remove(c);
    });
  }
}
