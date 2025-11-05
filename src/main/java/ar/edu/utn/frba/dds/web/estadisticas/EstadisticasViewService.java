package ar.edu.utn.frba.dds.web.estadisticas;

import ar.edu.utn.frba.dds.estadistica.Estadistica;
import ar.edu.utn.frba.dds.estadistica.TipoEstadistica;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import java.time.LocalDateTime;
import java.util.List;

public class EstadisticasViewService implements WithSimplePersistenceUnit {

  public List<Estadistica> obtenerUltimas() {
    return withTransaction(() -> {
      LocalDateTime ultima = entityManager()
          .createQuery("SELECT MAX(e.fechaCalculo) FROM Estadistica e", LocalDateTime.class)
          .getSingleResult();
      if (ultima == null) {
        return List.of();
      }
      return entityManager()
          .createQuery("SELECT e FROM Estadistica e WHERE e.fechaCalculo = :fecha", Estadistica.class)
          .setParameter("fecha", ultima)
          .getResultList();
    });
  }

  public List<Estadistica> obtenerPorTipo(TipoEstadistica tipo) {
    return withTransaction(() -> entityManager()
        .createQuery("SELECT e FROM Estadistica e WHERE e.tipo = :tipo", Estadistica.class)
        .setParameter("tipo", tipo)
        .getResultList());
  }
}
