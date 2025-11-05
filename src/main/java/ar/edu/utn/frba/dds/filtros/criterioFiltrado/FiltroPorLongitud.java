package ar.edu.utn.frba.dds.filtros.criterioFiltrado;

import ar.edu.utn.frba.dds.hecho.Hecho;
import ar.edu.utn.frba.dds.hecho.Lugar;

import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class FiltroPorLongitud implements CriterioFiltrado {

  private final String Longitud;

  public FiltroPorLongitud(String Longitud) {
    this.Longitud = Longitud;
  }

  @Override
  public boolean aplicar(Hecho hecho) {
    if (Longitud == null || Longitud.isBlank()) {
      return true;
    }
    if (hecho.getLugar() == null) {
      return false;
    }
    String LongitudHecho = hecho.getLugar().getLongitud();
    return LongitudHecho != null && Longitud.equalsIgnoreCase(LongitudHecho);
  }

  @Override
  public void agregarPredicados(CriteriaBuilder builder,
                                Root<Hecho> root,
                                List<Predicate> predicates) {

    if (Longitud == null || Longitud.isBlank()) {
      return;
    }

    // JOIN directo con Lugar
    Join<Hecho, Lugar> joinLugar = root.join("lugar", JoinType.INNER);

    // Filtramos por igualdad de Longitud
    predicates.add(
        builder.equal(
            joinLugar.get("Longitud"),
            Longitud
        )
    );
  }
}
