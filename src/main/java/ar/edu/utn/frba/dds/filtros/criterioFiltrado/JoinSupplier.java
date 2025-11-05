package ar.edu.utn.frba.dds.filtros.criterioFiltrado;

import ar.edu.utn.frba.dds.hecho.Hecho;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;

public interface JoinSupplier {
  <Y> Join<Hecho, Y> join(String attributeName, JoinType joinType);
}
