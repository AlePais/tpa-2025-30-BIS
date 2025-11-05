package ar.edu.utn.frba.dds.filtros.criterioFiltrado;

import ar.edu.utn.frba.dds.hecho.Hecho;
import java.util.List;
import java.util.Locale;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class FiltroPorCategoria implements CriterioFiltrado {

  private final String categoria;

  public FiltroPorCategoria(String categoria) {
    this.categoria = categoria;
  }

  @Override
  public boolean aplicar(Hecho hecho) {
    if (categoria == null || categoria.isBlank()) {
      return true;
    }
    String categoriaHecho = hecho.getCategoria();
    return categoriaHecho != null && categoria.equalsIgnoreCase(categoriaHecho);
  }

  @Override
  public void agregarPredicados(CriteriaBuilder builder,
                                Root<Hecho> root,
                                JoinSupplier joinSupplier,
                                List<Predicate> predicates) {
    if (categoria == null || categoria.isBlank()) {
      return;
    }
    predicates.add(builder.equal(builder.lower(root.get("categoria")),
        categoria.toLowerCase(Locale.ROOT)));
  }
}
