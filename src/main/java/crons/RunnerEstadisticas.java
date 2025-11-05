package crons;

import ar.edu.utn.frba.dds.estadistica.repositorio.RepositorioEstadisticasJPA;
import ar.edu.utn.frba.dds.estadistica.servicio.ServicioEstadisticas;
import ar.edu.utn.frba.dds.hecho.Hecho;
import ar.edu.utn.frba.dds.gestor_solicitudes_eliminacion.SolicitudEliminacion;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class RunnerEstadisticas {

  public static void main(String[] args) {
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("metamapaPU");
    EntityManager em = emf.createEntityManager();

    RepositorioEstadisticasJPA repo = new RepositorioEstadisticasJPA();
    repo.setEntityManager(em); // si lo configurás así

    ServicioEstadisticas servicio = new ServicioEstadisticas(repo);

    // TODO: obtener hechos y solicitudes desde donde corresponda
    List<Hecho> hechos = cargarHechos(em);
    List<SolicitudEliminacion> solicitudes = cargarSolicitudes(em);

    servicio.recalcularEstadisticas(hechos, solicitudes);

    System.out.println("✅ Estadísticas recalculadas correctamente.");
    em.close();
    emf.close();
  }

  private static List<Hecho> cargarHechos(EntityManager em) {
    return em.createQuery("SELECT h FROM Hecho h", Hecho.class).getResultList();
  }

  private static List<SolicitudEliminacion> cargarSolicitudes(EntityManager em) {
    return em.createQuery("SELECT s FROM SolicitudEliminacion s", SolicitudEliminacion.class)
        .getResultList();
  }
}
