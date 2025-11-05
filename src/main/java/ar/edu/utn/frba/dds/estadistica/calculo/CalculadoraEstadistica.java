package ar.edu.utn.frba.dds.estadistica.calculo;

import ar.edu.utn.frba.dds.estadistica.Estadistica;
import ar.edu.utn.frba.dds.estadistica.TipoEstadistica;
import ar.edu.utn.frba.dds.hecho.Hecho;
import java.util.List;

public interface CalculadoraEstadistica {
  TipoEstadistica getTipo();
  List<Estadistica> calcular(List<Hecho> hechos);
}