package ar.edu.utn.frba.dds.estadistica.servicio;

import ar.edu.utn.frba.dds.estadistica.Estadistica;
import ar.edu.utn.frba.dds.estadistica.TipoEstadistica;
import ar.edu.utn.frba.dds.estadistica.repositorio.RepositorioEstadisticas;
import ar.edu.utn.frba.dds.estadistica.calculo.CalculadoraEstadistica;
import ar.edu.utn.frba.dds.estadistica.calculo.*;

import ar.edu.utn.frba.dds.hecho.Hecho;
import ar.edu.utn.frba.dds.gestor_solicitudes_eliminacion.SolicitudEliminacion;

import java.util.ArrayList;
import java.util.List;

public class ServicioEstadisticas {

  private final RepositorioEstadisticas repoEstadisticas;

  public ServicioEstadisticas(RepositorioEstadisticas repoEstadisticas) {
    this.repoEstadisticas = repoEstadisticas;
  }

  public void recalcularEstadisticas(List<Hecho> hechos, List<SolicitudEliminacion> solicitudes) {
    List<CalculadoraEstadistica> calculadoras = new ArrayList<>();
    calculadoras.add(new HechosPorProvincia());
    calculadoras.add(new HechosPorCategoria());
    calculadoras.add(new HechosPorProvinciaYCategoria());
    calculadoras.add(new HechosPorHora());
    calculadoras.add(new SolicitudesSpam(solicitudes));

    for (CalculadoraEstadistica calc : calculadoras) {
      List<Estadistica> resultados = calc.calcular(hechos);
      repoEstadisticas.guardarTodas(resultados);
    }
  }

  public List<Estadistica> obtenerUltimaCorrida() {
    return repoEstadisticas.buscarUltimaCorrida();
  }

  public List<Estadistica> obtenerTodas() {
    return repoEstadisticas.buscarTodas();
  }

  public List<Estadistica> obtenerPorTipo(TipoEstadistica tipo) {
    return repoEstadisticas.buscarPorTipo(tipo);
  }

}
