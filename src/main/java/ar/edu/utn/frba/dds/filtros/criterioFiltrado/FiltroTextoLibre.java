package ar.edu.utn.frba.dds.filtros.criterioFiltrado;

import ar.edu.utn.frba.dds.hecho.Hecho;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class FiltroTextoLibre implements CriterioFiltrado {

  private final String termino;

  public FiltroTextoLibre(String termino) {
    this.termino = termino;
  }

  @Override
  public boolean aplicar(Hecho hecho) {
    if (termino == null || termino.isBlank()) {
      return true;
    }
    String terminoNormalizado = termino.toLowerCase(Locale.ROOT);
    return Stream.of(hecho.getTitulo(), hecho.getDescripcion())
        .filter(Objects::nonNull)
        .map(texto -> texto.toLowerCase(Locale.ROOT))
        .anyMatch(texto -> texto.contains(terminoNormalizado));
  }

  @Override
  public void agregarPredicados(CriteriaBuilder builder,
                                Root<Hecho> root,
                                JoinSupplier joinSupplier,
                                List<Predicate> predicates) {
    if (termino == null || termino.isBlank()) {
      return;
    }

    String patron = "%" + termino.toLowerCase(Locale.ROOT) + "%";
    Predicate fallback = builder.or(
        builder.like(builder.lower(root.get("titulo")), patron),
        builder.like(builder.lower(root.get("descripcion")), patron)
    );

    Expression<Double> score = builder.function(
        "match_against",
        Double.class,
        root.get("titulo"),
        root.get("descripcion"),
        builder.literal(armarConsultaBooleanMode())
    );

    predicates.add(builder.or(builder.greaterThan(score, 0d), fallback));
  }

  private String armarConsultaBooleanMode() {
    String consulta = Arrays.stream(termino.trim().split("\\s+"))
        .filter(palabra -> !palabra.isBlank())
        .map(this::prepararToken)
        .filter(token -> !token.isBlank())
        .collect(Collectors.joining(" "));
    return consulta.isBlank() ? termino.trim() : consulta;
  }

  private String prepararToken(String token) {
    String limpio = token.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9áéíóúüñ]", "");
    if (limpio.isBlank()) {
      return "";
    }
    if (limpio.endsWith("*")) {
      return limpio;
    }
    return limpio + "*";
  }
}
