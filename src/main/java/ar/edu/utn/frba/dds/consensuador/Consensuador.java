package ar.edu.utn.frba.dds.consensuador;

import ar.edu.utn.frba.dds.hecho.Hecho;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

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

  public Map<String, EnumSet<CriterioConsenso>> procesarConsensos(List<Hecho> hechos, int numeroFuentes) {
    Map<String, List<Hecho>> hechosPorTitulo = new LinkedHashMap<>();
    for (Hecho hecho : hechos) {
      if (hecho == null) {
        continue;
      }
      String clave = normalizarTitulo(hecho.getTitulo());
      if (clave == null) {
        continue;
      }
      hechosPorTitulo.computeIfAbsent(clave, key -> new ArrayList<>()).add(hecho);
    }

    Map<String, EnumSet<CriterioConsenso>> consensos = new LinkedHashMap<>();
    for (Map.Entry<String, List<Hecho>> entry : hechosPorTitulo.entrySet()) {
      List<Hecho> hechosDelGrupo = entry.getValue();
      EnumSet<CriterioConsenso> criteriosCumplidos = EnumSet.noneOf(CriterioConsenso.class);
      for (CriterioConsenso criterio : criterios) {
        boolean algunoCumple = hechosDelGrupo.stream()
            .anyMatch(h -> criterio.esConsensuado(h, hechosDelGrupo, numeroFuentes));
        if (algunoCumple) {
          criteriosCumplidos.add(criterio);
        }
      }
      consensos.put(entry.getKey(), criteriosCumplidos);
    }
    return consensos;
  }

  private String normalizarTitulo(String titulo) {
    return titulo == null ? null : titulo.toLowerCase(Locale.ROOT).trim();
  }
}
