package ar.edu.utn.frba.dds.estadistica.calculo;

import ar.edu.utn.frba.dds.estadistica.Estadistica;
import ar.edu.utn.frba.dds.estadistica.TipoEstadistica;
import ar.edu.utn.frba.dds.hecho.Hecho;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class HechosPorProvincia implements CalculadoraEstadistica {

  @Override
  public TipoEstadistica getTipo() {
    return TipoEstadistica.HECHOS_POR_PROVINCIA;
  }

  @Override
  public List<Estadistica> calcular(List<Hecho> hechos) {
    Map<String, Long> conteo = hechos.stream()
        .map(h -> {
          if (h.getLugar() == null || h.getLugar().getProvincia() == null) {
            return "SIN_PROVINCIA";
          }
          return h.getLugar().getProvincia();
        })
        .collect(Collectors.groupingBy(p -> p, Collectors.counting()));

    return conteo.entrySet().stream()
        .map(e -> new Estadistica(getTipo(), e.getKey(), e.getValue()))
        .toList();
  }
}
