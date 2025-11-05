package ar.edu.utn.frba.dds.estadistica.calculo;

import ar.edu.utn.frba.dds.estadistica.Estadistica;
import ar.edu.utn.frba.dds.estadistica.TipoEstadistica;
import ar.edu.utn.frba.dds.gestor_solicitudes_eliminacion.SolicitudEliminacion;
import ar.edu.utn.frba.dds.hecho.Hecho;
import java.util.List;

public class SolicitudesSpam implements CalculadoraEstadistica {

  private final List<SolicitudEliminacion> solicitudes;

  public SolicitudesSpam(List<SolicitudEliminacion> solicitudes) {
    this.solicitudes = solicitudes;
  }

  @Override
  public TipoEstadistica getTipo() {
    return TipoEstadistica.SOLICITUDES_SPAM;
  }

  @Override
  public List<Estadistica> calcular(List<Hecho> hechos) {
    long totalSpam = solicitudes.stream()
        .filter(SolicitudEliminacion::isSpam)
        .count();

    return List.of(new Estadistica(getTipo(), "Total", totalSpam));
  }
}
