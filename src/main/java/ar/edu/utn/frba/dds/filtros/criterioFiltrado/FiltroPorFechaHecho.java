package ar.edu.utn.frba.dds.filtros.criterioFiltrado;

import ar.edu.utn.frba.dds.hecho.Hecho;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class FiltroPorFechaHecho implements CriterioFiltrado {

  private final LocalDate fechaHecho;

  public FiltroPorFechaHecho(LocalDate fechaHecho) {
    this.fechaHecho = fechaHecho;
  }

  @Override
  public boolean aplicar(Hecho hecho) {
    if (fechaHecho == null) {
      return true;
    }
    LocalDateTime fechaAcontecimiento = hecho.getFechaAcontecimiento();
    return fechaAcontecimiento != null && fechaHecho.equals(fechaAcontecimiento.toLocalDate());
  }

  @Override
  public void agregarPredicados(CriteriaBuilder builder,
                                Root<Hecho> root,
                                JoinSupplier joinSupplier,
                                List<Predicate> predicates) {
    if (fechaHecho == null) {
      return;
    }
    LocalDateTime desde = fechaHecho.atStartOfDay();
    LocalDateTime hasta = fechaHecho.plusDays(1).atStartOfDay();
    predicates.add(builder.greaterThanOrEqualTo(root.get("fechaAcontecimiento"), desde));
    predicates.add(builder.lessThan(root.get("fechaAcontecimiento"), hasta));
  }
}
