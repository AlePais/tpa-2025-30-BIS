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
import javax.persistence.Column;
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
@Entity
@Table(name = "Colecciones")
public class Coleccion {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private String titulo;
  private String descripcion;
  @Column(name = "handle", nullable = false, unique = true, length = 64)
  private String handle;
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
  }



  @PostLoad
  private void initializeGestor() {
    getGestorInternal();
  }

  public void agregarCriterioPertenencia(ParametroFiltro criterio) {
    this.criteriosPertenencia.add(criterio);
  }

  public void agregarCriterioFiltrado(ParametroFiltro criterio){
    this.criteriosFiltrado.add(criterio);
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getTitulo() {
    return titulo;
  }

  public void setTitulo(String titulo) {
    this.titulo = titulo;
  }

  public String getDescripcion() {
    return descripcion;
  }

  public void setDescripcion(String descripcion) {
    this.descripcion = descripcion;
  }

  public Fuente getFuente() {
    return fuente;
  }

  public void setFuente(Fuente fuente) {
    this.fuente = fuente;
  }

  public Boolean getNavegacionCurada() {
    return navegacionCurada;
  }

  public void setNavegacionCurada(Boolean navegacionCurada) {
    this.navegacionCurada = navegacionCurada;
  }

  public CriterioConsenso getCriterioConsenso() {
    return criterioConsenso;
  }

  public void setCriterioConsenso(CriterioConsenso criterioConsenso) {
    this.criterioConsenso = criterioConsenso;
  }

  public GestorSolicitudesEliminacion getGestor() {
    if (this.gestor == null) {
      getGestorInternal();
    }
    return this.gestor;
  }

  public void setGestor(GestorSolicitudesEliminacion gestor) {
    this.gestor = gestor;
  }

  public List<ParametroFiltro> getCriteriosPertenencia() {
    return criteriosPertenencia;
  }

  public void setCriteriosPertenencia(List<ParametroFiltro> criteriosPertenencia) {
    this.criteriosPertenencia = criteriosPertenencia;
  }

  public List<ParametroFiltro> getCriteriosFiltrado() {
    return criteriosFiltrado;
  }

  public void setCriteriosFiltrado(List<ParametroFiltro> criteriosFiltrado) {
    this.criteriosFiltrado = criteriosFiltrado;
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
      getGestorInternal();
    }
    return hechos.stream()
        .filter(hecho -> !gestor.siEstaEliminado(hecho.getTitulo()))
        .toList();
  }

  private void getGestorInternal() {
    this.gestor = GestorSolicitudesEliminacion.getInstancia();
  }
}
