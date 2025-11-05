package ar.edu.utn.frba.dds.hecho;

import ar.edu.utn.frba.dds.consensuador.CriterioConsenso;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorValue;
import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
@Table(name = "Hechos")
@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "DTYPE")
@DiscriminatorValue("HECHO")
public class Hecho {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String titulo;
  private String descripcion;
  private String categoria;
  private String multimedia;
  @OneToOne
  @JoinColumn(name = "id_lugar")
  private Lugar lugar;
  private LocalDateTime fechaAcontecimiento;
  private LocalDateTime fechaCarga;
  @Enumerated(EnumType.STRING)
  private OrigenHecho origen;
  @ElementCollection(targetClass = CriterioConsenso.class, fetch = FetchType.EAGER)
  @CollectionTable(name = "hechos_criterios_consenso", joinColumns = @JoinColumn(name = "hecho_id"))
  @Enumerated(EnumType.STRING)
  @Column(name = "criterio")
  private Set<CriterioConsenso> criteriosConsenso = new java.util.HashSet<>();

  protected Hecho(Builder builder) {
    this.titulo              = builder.titulo;
    this.descripcion         = builder.descripcion;
    this.categoria           = builder.categoria;
    this.multimedia          = builder.multimedia;
    this.lugar               = builder.lugar;
    this.fechaAcontecimiento = builder.fechaAcontecimiento;
    this.fechaCarga          = builder.fechaCarga != null
        ? builder.fechaCarga
        : LocalDateTime.now();
    this.origen              = builder.origen;
  }

  public String getLatitud() {
    return lugar.getLatitud();
  }

  public String getLongitud() {
    return lugar.getLongitud();
  }

  public void agregarCriterioConsenso(CriterioConsenso criterio) {
    criteriosConsenso.add(criterio);
  }

  public void setCriterioConsenso(Set<CriterioConsenso> criteriosConsenso) {
    this.criteriosConsenso = criteriosConsenso;
  }

  public void limpiarCriterioConsenso() {
    criteriosConsenso.clear();
  }

  public boolean cumpleCriterio(CriterioConsenso criterioConsenso) {
    return this.criteriosConsenso.contains(criterioConsenso);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Hecho)) return false;
    Hecho hecho = (Hecho) o;
    return Objects.equals(titulo, hecho.titulo);
  }

  public boolean equalsTotal(Object o) {
    if (this == o) return true;
    if (!(o instanceof Hecho)) return false;
    Hecho hecho = (Hecho) o;
    return Objects.equals(titulo, hecho.titulo) &&
        Objects.equals(descripcion, hecho.descripcion) &&
        Objects.equals(categoria, hecho.categoria) &&
        Objects.equals(lugar, hecho.lugar) &&
        Objects.equals(fechaAcontecimiento, hecho.fechaAcontecimiento);// &&origen == hecho.origen;

  }

  @Override
  public int hashCode() {
    return Objects.hash(titulo, descripcion, categoria, lugar, fechaAcontecimiento); //, origen
  }

  // Builder anidado
  public static class Builder {
    private String titulo;
    private String descripcion;
    private String categoria;
    private String multimedia;
    private Lugar lugar;
    private LocalDateTime fechaAcontecimiento;
    private LocalDateTime fechaCarga;
    private OrigenHecho origen;
    private String provincia;

    public Builder titulo(String titulo) {
      this.titulo = titulo;
      return this;
    }

    public Builder descripcion(String descripcion) {
      this.descripcion = descripcion;
      return this;
    }

    public Builder categoria(String categoria) {
      this.categoria = categoria;
      return this;
    }

    public Builder multimedia(String multimedia) {
      this.multimedia = multimedia;
      return this;
    }

    public Builder lugar(String latitud, String longitud, String provincia) {
      this.lugar = new Lugar(latitud, longitud, provincia);
      return this;
    }

    public Builder lugar(Lugar lugar) {
      this.lugar = lugar;
      return this;
    }

    public Builder fechaAcontecimiento(LocalDateTime fecha) {
      this.fechaAcontecimiento = fecha;
      return this;
    }

    public Builder fechaCarga(LocalDateTime fechaCarga) {
      this.fechaCarga = fechaCarga;
      return this;
    }

    public Builder origen(OrigenHecho origen) {
      this.origen = origen;
      return this;
    }

    public boolean isComplete() {
      return titulo != null && descripcion != null && categoria != null &&
          lugar != null && fechaAcontecimiento != null;
    }

    public Hecho build() {
      if (!isComplete()) {
        throw new IllegalStateException("Faltan campos obligatorios para construir el Hecho.");
      }
      return new Hecho(this);
    }
  }
}
