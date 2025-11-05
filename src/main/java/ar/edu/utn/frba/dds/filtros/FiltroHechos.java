package ar.edu.utn.frba.dds.filtros;

import ar.edu.utn.frba.dds.consensuador.CriterioConsenso;
import ar.edu.utn.frba.dds.filtros.criterioFiltrado.*;
import ar.edu.utn.frba.dds.hecho.Hecho;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public class FiltroHechos implements WithSimplePersistenceUnit {
  private final List<CriterioFiltrado> criteriosEstaticos;
  private final List<CriterioFiltrado> criteriosSoloDinamicos;

  public FiltroHechos(List<CriterioFiltrado> criteriosEstaticos,
                      List<CriterioFiltrado> criteriosSoloDinamicos) {
    this.criteriosEstaticos = criteriosEstaticos;
    this.criteriosSoloDinamicos = criteriosSoloDinamicos;
  }

  public List<Hecho> obtenerHechos(List<Hecho> hechos) {
    List<Hecho> hechosEstaticos = hechos.stream()
        .filter(hecho -> criteriosEstaticos.stream().allMatch(criterio -> criterio.aplicar(hecho)))
        .toList();

    CriteriaBuilder builder = entityManager().getCriteriaBuilder();
    CriteriaQuery<Hecho> query = builder.createQuery(Hecho.class);
    Root<Hecho> root = query.from(Hecho.class);
    query.select(root).distinct(true);

    List<Predicate> predicates = new ArrayList<>();
    List<CriterioFiltrado> criteriosParaQuery = new ArrayList<>(criteriosEstaticos);
    criteriosParaQuery.addAll(criteriosSoloDinamicos);
    for (CriterioFiltrado criterio : criteriosParaQuery) {
      criterio.agregarPredicados(builder, root, predicates);
    }

    if (!predicates.isEmpty()) {
      query.where(builder.and(predicates.toArray(new Predicate[0])));
    }

    List<Hecho> hechosPersistidos = withTransaction(() ->
        entityManager().createQuery(query).getResultList()
    );

    Map<String, Hecho> combinados = new LinkedHashMap<>();
    agregarAlMapa(combinados, hechosEstaticos);
    agregarAlMapa(combinados, hechosPersistidos);

    return new ArrayList<>(combinados.values());
  }

  private void agregarAlMapa(Map<String, Hecho> destino, List<Hecho> hechos) {
    for (Hecho hecho : hechos) {
      String clave = hecho != null ? normalizarTitulo(hecho.getTitulo()) : null;
      if (clave == null) {
        continue;
      }
      destino.put(clave, hecho);
    }
  }

  private String normalizarTitulo(String titulo) {
    return titulo == null ? null : titulo.toLowerCase(Locale.ROOT).trim();
  }

  public static class Builder {

    private final List<CriterioFiltrado> criteriosEstaticos = new ArrayList<>();
    private final List<CriterioFiltrado> criteriosSoloDinamicos = new ArrayList<>();
    private final List<ParametroFiltro> parametros = new ArrayList<>();
    private Boolean navegacionCurada;

    public Builder agregarCriterio(ParametroFiltro criterio){
      if (criterio != null) {
        this.parametros.add(criterio);
      }
      return this;
    }

    public Builder agregarCriterios(List<ParametroFiltro> criterios) {
      if (criterios != null) {
        criterios.forEach(this::agregarCriterio);
      }
      return this;
    }

    public Builder navegacionCurada(Boolean navegacionCurada) {
      this.navegacionCurada = navegacionCurada;
      return this;
    }

    private void agregarEstatico(CriterioFiltrado criterio) {
      criteriosEstaticos.add(criterio);
    }

    private void agregarSoloDinamico(CriterioFiltrado criterio) {
      criteriosSoloDinamicos.add(criterio);
    }

    private void registrarFiltroDoble(CriterioFiltrado filtro) {
      agregarEstatico(filtro);
      agregarSoloDinamico(filtro);
    }

    public Builder criterioConsenso(CriterioConsenso criterioConsenso) {
      if (Boolean.TRUE.equals(this.navegacionCurada) && criterioConsenso != null) {
        FiltroPorCriterioConsenso filtro = new FiltroPorCriterioConsenso(criterioConsenso);
        registrarFiltroDoble(filtro);
      }
      return this;
    }

    public FiltroHechos build() {
      for (ParametroFiltro parametro : parametros) {
        String valor = parametro.getValor();
        if (valor == null || valor.isBlank()) {
          continue;
        }
        switch (parametro.getAtributoFiltro()) {
          case CATEGORIA -> registrarFiltroDoble(new FiltroPorCategoria(valor));
          case CRITERIO_CONSENSO -> {
            final CriterioConsenso criterioConsenso;
            try {
              criterioConsenso = CriterioConsenso.valueOf(valor);
            } catch (IllegalArgumentException ex) {
              throw new IllegalArgumentException("Valor de criterio de consenso inválido: "
                  + valor, ex);
            }
            FiltroPorCriterioConsenso filtro = new FiltroPorCriterioConsenso(criterioConsenso);
            registrarFiltroDoble(filtro);
          }
          case DESCRIPCION -> registrarFiltroDoble(new FiltroPorDescripcion(valor));
          case FECHA_HECHO -> {
            try {
              registrarFiltroDoble(new FiltroPorFechaHecho(LocalDate.parse(valor)));
            } catch (DateTimeParseException ex) {
              throw new IllegalArgumentException("Formato de fecha inválido, se esperaba yyyy-MM-dd: " + valor, ex);
            }
          }
          case LATITUD -> registrarFiltroDoble(new FiltroPorLatitud(valor));
          case LONGITUD -> registrarFiltroDoble(new FiltroPorLongitud(valor));
          case TITULO -> registrarFiltroDoble(new FiltroPorTitulo(valor));
          default -> throw new RuntimeException("No existe tipo de criterio");
        }
      }
      return new FiltroHechos(new ArrayList<>(this.criteriosEstaticos),
          new ArrayList<>(this.criteriosSoloDinamicos));
    }

  }
}
