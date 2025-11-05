package ar.edu.utn.frba.dds.filtros.criterioFiltrado;

import ar.edu.utn.frba.dds.hecho.Hecho;
import java.util.List;
import java.util.Locale;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class FiltroPorTitulo implements CriterioFiltrado {

  private final String titulo;

  public FiltroPorTitulo(String titulo) {
    this.titulo = titulo;
  }

  @Override
  public boolean aplicar(Hecho hecho) {
    if (titulo == null || titulo.isBlank()) {
      return true;
    }
    String tituloHecho = hecho.getTitulo();
    return tituloHecho != null
        && tituloHecho.toLowerCase(Locale.ROOT).contains(titulo.toLowerCase(Locale.ROOT));
  }

  @Override
  public void agregarPredicados(CriteriaBuilder builder,
                                Root<Hecho> root,
                                List<Predicate> predicates) {
    if (titulo == null || titulo.isBlank()) {
      return;
    }
    predicates.add(builder.like(builder.lower(root.get("titulo")),
        "%" + titulo.toLowerCase(Locale.ROOT) + "%"));
  }
}
