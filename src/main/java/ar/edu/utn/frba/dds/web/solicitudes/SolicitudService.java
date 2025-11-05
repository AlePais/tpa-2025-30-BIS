package ar.edu.utn.frba.dds.web.solicitudes;

import ar.edu.utn.frba.dds.gestor_solicitudes_eliminacion.GestorSolicitudesEliminacion;
import ar.edu.utn.frba.dds.gestor_solicitudes_eliminacion.SolicitudEliminacion;
import java.util.List;

public class SolicitudService {

  private final GestorSolicitudesEliminacion gestor;

  public SolicitudService(GestorSolicitudesEliminacion gestor) {
    this.gestor = gestor;
  }

  public void crearSolicitud(String titulo, String motivo) {
    SolicitudEliminacion solicitud = new SolicitudEliminacion(titulo, motivo);
    gestor.agregarSolicitud(solicitud);
  }

  public List<SolicitudEliminacion> pendientes() {
    return gestor.getSolicitudesPendientes();
  }

  public void aprobar(Long id) {
    SolicitudEliminacion solicitud = gestor.buscarSolicitudPorId(id);
    if (solicitud != null) {
      gestor.aprobarSolicitud(solicitud);
    }
  }

  public void rechazar(Long id) {
    SolicitudEliminacion solicitud = gestor.buscarSolicitudPorId(id);
    if (solicitud != null) {
      gestor.desaprobarSolicitud(solicitud);
    }
  }
}
