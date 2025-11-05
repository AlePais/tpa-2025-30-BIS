package ar.edu.utn.frba.dds.filtros.criterioFiltrado;

import ar.edu.utn.frba.dds.hecho.Hecho;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public interface CriterioFiltrado {
  boolean aplicar(Hecho hecho);

  void agregarPredicados(CriteriaBuilder builder,
                         Root<Hecho> root,
                         List<Predicate> predicates);
}
