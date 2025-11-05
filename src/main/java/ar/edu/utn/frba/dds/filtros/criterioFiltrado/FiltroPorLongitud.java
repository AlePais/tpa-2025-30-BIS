package ar.edu.utn.frba.dds.filtros.criterioFiltrado;

import ar.edu.utn.frba.dds.hecho.Hecho;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class FiltroPorLongitud implements CriterioFiltrado {

  private final String longitud;

  public FiltroPorLongitud(String longitud) {
    this.longitud = longitud;
  }

  @Override
  public boolean aplicar(Hecho hecho) {
    if (longitud == null || longitud.isBlank()) {
      return true;
    }
    if (hecho.getLugar() == null) {
      return false;
    }
    String longitudHecho = hecho.getLugar().getLongitud();
    return longitudHecho != null && longitud.equalsIgnoreCase(longitudHecho);
  }

  @Override
  public void agregarPredicados(CriteriaBuilder builder,
                                Root<Hecho> root,
                                JoinSupplier joinSupplier,
                                List<Predicate> predicates) {
    if (longitud == null || longitud.isBlank()) {
      return;
    }
    predicates.add(builder.equal(
        joinSupplier.join("lugar", JoinType.INNER).get("longitud"), longitud));
  }
}
