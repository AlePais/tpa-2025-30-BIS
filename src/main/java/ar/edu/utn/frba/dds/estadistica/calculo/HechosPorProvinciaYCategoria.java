package ar.edu.utn.frba.dds.estadistica.calculo;

import ar.edu.utn.frba.dds.estadistica.Estadistica;
import ar.edu.utn.frba.dds.estadistica.TipoEstadistica;
import ar.edu.utn.frba.dds.hecho.Hecho;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HechosPorProvinciaYCategoria implements CalculadoraEstadistica {

  @Override
  public TipoEstadistica getTipo() {
    return TipoEstadistica.HECHOS_POR_PROVINCIA_Y_CATEGORIA;
  }

  @Override
  public List<Estadistica> calcular(List<Hecho> hechos) {
    Map<String, Long> conteo = hechos.stream()
        .collect(Collectors.groupingBy(
            h -> h.getLugar().getProvincia() + " - " + h.getCategoria(),
            Collectors.counting()
        ));

    return conteo.entrySet().stream()
        .map(e -> new Estadistica(getTipo(), e.getKey(), e.getValue()))
        .toList();
  }
}