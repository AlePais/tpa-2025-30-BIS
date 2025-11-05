package ar.edu.utn.frba.dds.filtros.criterioFiltrado;

import ar.edu.utn.frba.dds.hecho.Hecho;
import ar.edu.utn.frba.dds.hecho.Lugar;

import java.util.List;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Join;
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
                                List<Predicate> predicates) {

    if (latitud == null || latitud.isBlank()) {
      return;
    }

    // JOIN directo con Lugar
    Join<Hecho, Lugar> joinLugar = root.join("lugar", JoinType.INNER);

    // Filtramos por igualdad de latitud
    predicates.add(
        builder.equal(
            joinLugar.get("latitud"),
            latitud
        )
    );
  }
}
