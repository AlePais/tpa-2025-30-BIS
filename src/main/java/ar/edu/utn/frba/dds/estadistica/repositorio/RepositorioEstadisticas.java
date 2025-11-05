package ar.edu.utn.frba.dds.estadistica.repositorio;

import ar.edu.utn.frba.dds.estadistica.Estadistica;
import ar.edu.utn.frba.dds.estadistica.TipoEstadistica;
import java.util.List;

public interface RepositorioEstadisticas {
  void guardar(Estadistica estadistica);
  void guardarTodas(List<Estadistica> estadisticas);

  List<Estadistica> buscarPorTipo(TipoEstadistica tipo);
  List<Estadistica> buscarTodas();
  List<Estadistica> buscarUltimaCorrida();

  void limpiar(); // útil para recalcular estadísticas periódicamente
}