package ar.edu.utn.frba.dds.filtros.criterioFiltrado;

import ar.edu.utn.frba.dds.hecho.Hecho;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class FiltroPorLatitud implements CriterioFiltrado {

  private final String latitud;

  public FiltroPorLatitud(String latitud) {
    this.latitud = latitud;
  }

  @Override
  public boolean aplicar(Hecho hecho) {
    if (latitud == null || latitud.isBlank()) {
      return true;
    }
    if (hecho.getLugar() == null) {
      return false;
    }
    String latitudHecho = hecho.getLugar().getLatitud();
    return latitudHecho != null && latitud.equalsIgnoreCase(latitudHecho);
  }

  @Override
  public void agregarPredicados(CriteriaBuilder builder,
                                Root<Hecho> root,
                                JoinSupplier joinSupplier,
                                List<Predicate> predicates) {
    if (latitud == null || latitud.isBlank()) {
      return;
    }
    predicates.add(builder.equal(
        joinSupplier.join("lugar", JoinType.INNER).get("latitud"), latitud));
  }
}
