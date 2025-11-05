package ar.edu.utn.frba.dds.coleccion;

import ar.edu.utn.frba.dds.consensuador.*;
import ar.edu.utn.frba.dds.filtros.ParametroFiltro;
import ar.edu.utn.frba.dds.filtros.FiltroHechos;
import ar.edu.utn.frba.dds.fuente.Fuente;
import ar.edu.utn.frba.dds.gestor_solicitudes_eliminacion.GestorSolicitudesEliminacion;
import ar.edu.utn.frba.dds.hecho.Hecho;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.PostLoad;
import javax.persistence.Table;
import javax.persistence.Transient;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "Colecciones")
public class Coleccion implements WithSimplePersistenceUnit {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String titulo;
  private String descripcion;
  @Transient
  private Fuente fuente;
  private Boolean navegacionCurada = false;
  @Enumerated(EnumType.STRING)
  private CriterioConsenso criterioConsenso;
  @Transient
  private GestorSolicitudesEliminacion gestor;

  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "coleccion_id")
  private List<ParametroFiltro> criteriosPertenencia = new ArrayList<>();

  @Transient
  private List<ParametroFiltro> criteriosFiltrado = new ArrayList<>();

  public Coleccion() {
  }

  public Coleccion(String titulo, String descripcion, Fuente fuente,
                   CriterioConsenso criterioConsenso) {
    this.titulo           = titulo;
    this.descripcion      = descripcion;
    this.fuente           = fuente;
    this.criterioConsenso = criterioConsenso;
    this.getGestor();
  }

  @PostLoad
  private void getGestor() {
    this.gestor = GestorSolicitudesEliminacion.getInstancia();
  }

  public void agregarCriterioPertenencia(ParametroFiltro criterio) {
    this.criteriosPertenencia.add(criterio);
  }

  public void agregarCriterioFiltrado(ParametroFiltro criterio){
    this.criteriosFiltrado.add(criterio);
  }

  public List<Hecho> obtenerHechos() {

    if (fuente == null) {
      return List.of();
    }

    FiltroHechos filtroHechos = new FiltroHechos
        .Builder()
        .agregarCriterios(this.criteriosPertenencia)
        .agregarCriterios(this.criteriosFiltrado)
        .navegacionCurada(this.navegacionCurada)
        .criterioConsenso(this.criterioConsenso)
        .build();

    List<Hecho> hechosFiltrados = filtroHechos.obtenerHechos(fuente.obtenerHechos());
    return quitarEliminados(hechosFiltrados);
  }

  private List<Hecho> quitarEliminados(List<Hecho> hechos){
    if (gestor == null) {
      getGestor();
    }
    return hechos.stream()
        .filter(hecho -> !gestor.siEstaEliminado(hecho.getTitulo()))
        .toList();
  }

}