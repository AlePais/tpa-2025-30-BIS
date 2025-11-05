package ar.edu.utn.frba.dds.gestor_solicitudes_eliminacion;

import ar.edu.utn.frba.dds.hecho.EstadoSolicitud;
import ar.edu.utn.frba.dds.spam.DetectorDeSpam;
import ar.edu.utn.frba.dds.spam.DetectorSpamLocal;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

import java.util.ArrayList;
import java.util.List;

public class GestorSolicitudesEliminacion implements WithSimplePersistenceUnit {
  /**Por ser singleton tenemos como atributo una instancia de el mismo*/
  private static GestorSolicitudesEliminacion instancia;
  /**No esta mas el guardado local ahora es en base de dato
  private final List<SolicitudEliminacion> solicitudes;*/
  private final DetectorDeSpam detectorSpam;

  private GestorSolicitudesEliminacion() {
    //this.solicitudes = new ArrayList<>();
    this.detectorSpam = new DetectorSpamLocal(); // o inyectalo si querés más control
  }

  public static GestorSolicitudesEliminacion getInstancia() {
    if (instancia == null) {
      instancia = new GestorSolicitudesEliminacion();
    }
    return instancia;
  }

  public void agregarSolicitud(SolicitudEliminacion solicitud) {
    if (solicitud == null) {
      throw new IllegalArgumentException("La solicitud no puede ser nula");
    }

    if (detectorSpam.esSpam(solicitud.getMotivo())) {
      solicitud.rechazarSpam();
    }

    withTransaction(() -> {
      entityManager().persist(solicitud);
      return null;
    });
  }

  public SolicitudEliminacion buscarSolicitudPorId(Long idSolicitud) {
      List<SolicitudEliminacion> solicitudes =  entityManager().createQuery("SELECT s FROM SolicitudEliminacion s where s.id = :id", SolicitudEliminacion.class)
              .setParameter("id", idSolicitud)
              .getResultList();

      return solicitudes.isEmpty() ? null : solicitudes.get(0);
  }

  public void aprobarSolicitud(SolicitudEliminacion solicitud) {
    /**Antes de utilizar el aprobar solicitud se debe usar el buscarSolicutdPorId para tener
       el objeto solicitud, al cual vamos a aprobar */
    //for (SolicitudEliminacion solicitudLista : solicitudes) {
    //  if (solicitudLista.equals(solicitud)) {
    //    solicitudLista.aprobar();
    //    return;
    //  }
    //}
    //throw new IllegalArgumentException("La solicitud no se encuentra registrada.");
    withTransaction(() -> {
      SolicitudEliminacion manejada = entityManager().merge(solicitud);
      manejada.aprobar();
      return null;
    });
  }

  public void desaprobarSolicitud(SolicitudEliminacion solicitud) {
    /**Antes de utilizar el desAprobar solicitud se debe usar el buscarSolicutdPorId para tener
       el objeto solicitud, al cual vamos a desaprobar */
    //if (solicitudes.contains(solicitud)) {
    //  solicitud.rechazar();
    //} else {
    //  throw new IllegalArgumentException("La solicitud no se encuentra registrada.");
    //}
    withTransaction(() -> {
      SolicitudEliminacion manejada = entityManager().merge(solicitud);
      manejada.rechazar();
      return null;
    });
  }

  public List<SolicitudEliminacion> getSolicitudesPendientes() {
    List<SolicitudEliminacion> pendientes = new ArrayList<>();
    //for (SolicitudEliminacion solicitud : solicitudes) {
    //  if (solicitud.getEstado() == EstadoSolicitud.PENDIENTE) {
    //    pendientes.add(solicitud);
    //  }
    //}
    pendientes = entityManager().createQuery("SELECT s FROM SolicitudEliminacion s where s.estado = :estado", SolicitudEliminacion.class)
            .setParameter("estado", EstadoSolicitud.PENDIENTE)
            .getResultList();

    return pendientes;
  }

  public boolean siEstaEliminado(String titulo) {
  //  return solicitudes.stream()
  //      .anyMatch(solicitud -> solicitud.getEstado() == EstadoSolicitud.APROBADO
  //          && solicitud.getTitulo().equalsIgnoreCase(titulo));
    Long cantidad = entityManager().createQuery("SELECT COUNT(s) FROM SolicitudEliminacion s WHERE s.estado=:estado AND s.titulo=:titulo", Long.class)
            .setParameter("estado", EstadoSolicitud.APROBADO)
            .setParameter("titulo", titulo)
            .getSingleResult();

    return cantidad > 0;
  }

  public int cantidadSolicitudesPendientes() {
    return getSolicitudesPendientes().size();
  }

  // Solo para tests
  public static void reset() {
    //solicitudes.clear();
      //TODO despues ver que pasa con esto
      System.out.println("se reseteo, guiño guiño");
  }
}