package ar.edu.utn.frba.dds.filtros.criterioFiltrado;

import ar.edu.utn.frba.dds.hecho.Hecho;
import java.util.List;
import java.util.Locale;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class FiltroPorDescripcion implements CriterioFiltrado {

  private final String descripcion;

  public FiltroPorDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  @Override
  public boolean aplicar(Hecho hecho) {
    if (descripcion == null || descripcion.isBlank()) {
      return true;
    }
    String descripcionHecho = hecho.getDescripcion();
    return descripcionHecho != null
        && descripcionHecho.toLowerCase(Locale.ROOT).contains(descripcion.toLowerCase(Locale.ROOT));
  }

  @Override
  public void agregarPredicados(CriteriaBuilder builder,
                                Root<Hecho> root,
                                JoinSupplier joinSupplier,
                                List<Predicate> predicates) {
    if (descripcion == null || descripcion.isBlank()) {
      return;
    }
    predicates.add(builder.like(builder.lower(root.get("descripcion")),
        "%" + descripcion.toLowerCase(Locale.ROOT) + "%"));
  }
}
