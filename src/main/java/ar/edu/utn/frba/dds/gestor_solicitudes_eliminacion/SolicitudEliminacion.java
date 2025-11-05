package ar.edu.utn.frba.dds.gestor_solicitudes_eliminacion;

import static ar.edu.utn.frba.dds.hecho.EstadoSolicitud.*;

import ar.edu.utn.frba.dds.hecho.EstadoSolicitud;

import javax.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Entity
@Table(name = "SolitudEliminaciones")
public class SolicitudEliminacion {
  @Setter
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String titulo;
  @Column(columnDefinition = "TEXT")
  private String motivo;
  @Enumerated(EnumType.STRING)
  private EstadoSolicitud estado;

  public SolicitudEliminacion(String titulo, String motivo) {
    this.titulo = titulo;
    setMotivo(motivo);
    this.estado = PENDIENTE;
  }

  public void setMotivo(String motivo) {
    if (motivo == null || motivo.length() < 500) {
      throw new IllegalArgumentException("El motivo debe tener al menos 500 caracteres.");
    }
    this.motivo = motivo;
  }

  public void aprobar() {
    this.estado = APROBADO;
  }

  public void rechazar() {
    this.estado = RECHAZADO;
  }

  public void rechazarSpam() {
    this.estado = SPAM;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    SolicitudEliminacion that = (SolicitudEliminacion) obj;
    return titulo != null && titulo.equalsIgnoreCase(that.titulo);
  }

  @Override
  public int hashCode() {
    return titulo != null ? titulo.toLowerCase().hashCode() : 0;
  }

  public boolean isSpam() {
    return this.estado == SPAM;
  }

  public void setSpam(boolean spam) {
    if (spam) {
      rechazarSpam();
    } else if (this.estado == SPAM) {
      this.estado = PENDIENTE;
    }
  }
}

