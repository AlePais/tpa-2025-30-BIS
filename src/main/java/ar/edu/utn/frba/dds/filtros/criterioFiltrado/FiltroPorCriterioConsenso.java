package ar.edu.utn.frba.dds.filtros.criterioFiltrado;

import ar.edu.utn.frba.dds.consensuador.CriterioConsenso;
import ar.edu.utn.frba.dds.hecho.Hecho;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class FiltroPorCriterioConsenso implements CriterioFiltrado {

  private final CriterioConsenso criterioConsenso;

  public FiltroPorCriterioConsenso(CriterioConsenso criterioConsenso) {
    this.criterioConsenso = criterioConsenso;
  }

  @Override
  public boolean aplicar(Hecho hecho) {
    if (criterioConsenso == null) {
      return true;
    }
    return hecho != null && hecho.cumpleCriterio(criterioConsenso);
  }

  @Override
  public void agregarPredicados(CriteriaBuilder builder,
                                Root<Hecho> root,
                                JoinSupplier joinSupplier,
                                List<Predicate> predicates) {
    if (criterioConsenso == null) {
      return;
    }
    predicates.add(builder.equal(
        joinSupplier.join("criteriosConsenso", JoinType.INNER), criterioConsenso));
  }
}
