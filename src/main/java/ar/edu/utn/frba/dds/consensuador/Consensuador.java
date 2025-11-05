package ar.edu.utn.frba.dds.consensuador;

import ar.edu.utn.frba.dds.hecho.Hecho;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class Consensuador {

  private final List<CriterioConsenso> criterios;
  private static Consensuador instancia;

  private Consensuador(List<CriterioConsenso> criterios) {
    this.criterios = Objects.requireNonNull(criterios);
  }

  public static void inicializar(List<CriterioConsenso> criterios) {
    if (instancia == null) {
      instancia = new Consensuador(criterios);
    }
  }

  public static Consensuador instancia() {
    if (instancia == null) {
      throw new IllegalStateException("Consensuador no fue inicializado");
    }
    return instancia;
  }

  public List<Hecho> procesarConsensos(List<Hecho> hechos, int numeroFuentes) {

    // Agrupamos hechos por título normalizado
    Map<String, List<Hecho>> hechosPorTitulo = hechos.stream()
        .filter(Objects::nonNull)
        .filter(h -> h.getTitulo() != null)
        .collect(Collectors.groupingBy(h -> normalizarTitulo(h.getTitulo())));

    List<Hecho> hechosConsensuados = new ArrayList<>();

    for (List<Hecho> grupo : hechosPorTitulo.values()) {

      Hecho representativo = grupo.get(0); // Podrías clonar si querés evitar side effects

      for (CriterioConsenso criterio : criterios) {
        boolean cumple = grupo.stream()
            .anyMatch(h -> criterio.esConsensuado(h, grupo, numeroFuentes));

        if (cumple) {
          representativo.agregarCriterioConsenso(criterio);
        }
      }

      hechosConsensuados.add(representativo);
    }

    return hechosConsensuados;
  }

  private String normalizarTitulo(String titulo) {
    return titulo.toLowerCase(Locale.ROOT).trim();
  }
}
