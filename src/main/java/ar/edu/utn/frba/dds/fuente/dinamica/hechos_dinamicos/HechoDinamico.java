package ar.edu.utn.frba.dds.fuente.dinamica.hechos_dinamicos;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import ar.edu.utn.frba.dds.hecho.Hecho;
import ar.edu.utn.frba.dds.hecho.Lugar;
import ar.edu.utn.frba.dds.hecho.OrigenHecho;
import lombok.Getter;

@Getter
@Entity
@DiscriminatorValue("DINAMICO")
public class HechoDinamico extends Hecho {


  private String usuario;
  private String sugerenciaDeCambio;
  @Enumerated(EnumType.STRING)
  private EstadoHechoDinamico estado;

  protected HechoDinamico() { super(); }
  public HechoDinamico(String titulo, String descripcion, String categoria, Lugar lugar,
                       LocalDateTime fechaAcontecimiento, String usuario){
    super(new Hecho.Builder()
        .titulo(titulo)
        .descripcion(descripcion)
        .categoria(categoria)
        .lugar(lugar)
        .fechaAcontecimiento(fechaAcontecimiento)
        .origen(OrigenHecho.CONTRIBUYENTE));

    this.usuario = usuario;
    this.estado = EstadoHechoDinamico.PENDIENTE;
  }

  // Metodos propios del hecho dinamico
  /**
   un hecho dinamico es aprobado
  */
  public void aprobar() {
    this.setEstado(EstadoHechoDinamico.APROBADO);
  }

  /**
   un hecho dinamico es desaprobado
   */
  public void rechazar() {
    this.setEstado(EstadoHechoDinamico.RECHAZADO);
  }

  /**
   un hecho dinamico es aprobado con sugerencia
   */
  public void aprobarConSugerencia() {
    this.setEstado(EstadoHechoDinamico.APROBADO_CON_SUGERENCIA);
  }

  /**
    consulta si el hechoDinamico aun esta dentro de los 7 dias
  */
  public boolean aunEsEditable() {
    if(usuario==null)
      return false;

    LocalDateTime fechaCarga = this.getFechaCarga();
    if (fechaCarga == null) {
      return false;
    }
    long diasTranscurridos = ChronoUnit.DAYS.between(fechaCarga, LocalDateTime.now());
    return diasTranscurridos < 7;
  }

  /**
    verifica si un usuario es dueño de el hecho
  */
  public boolean perteneceAlUsuario(String usuario){
    return this.usuario.equalsIgnoreCase(usuario);
  }

  /**
    solicita cambiar un hecho el cambio viene en forma de sugerencia
  */
  public void modificarHecho(String usuario, String sugerenciaDeCambio){
    if (!perteneceAlUsuario(usuario)){
      throw new SecurityException("No es dueño del hecho");
    }
    if(!aunEsEditable()){
      throw new RuntimeException("El hecho ya no se puede editar");
    }

    this.sugerenciaDeCambio = sugerenciaDeCambio;
    this.estado = EstadoHechoDinamico.PENDIENTE;
  }

  /**
   convierte un hechoDinamico en un hecho
   */
  public Hecho convertirAHecho(){
    if(this.getEstado() == EstadoHechoDinamico.RECHAZADO || this.getEstado() == EstadoHechoDinamico.PENDIENTE ) {
      throw new RuntimeException("solo puedo convetir hechos que esten estado aprobado");
    }

    return new Hecho.Builder()
        .titulo(this.getTitulo())
        .descripcion(this.getDescripcion())
        .categoria(this.getCategoria())
        .lugar(this.getLugar())//TODO esta linea esta raro
        .fechaAcontecimiento(this.getFechaAcontecimiento())
        .fechaCarga(this.getFechaCarga())
        .origen(OrigenHecho.CONTRIBUYENTE)
        .build();

  }

  // ------- FUNCIONES AUXILIARES -------
  /**
   Setea el estado de un hecho dinamico, para cuando fue aprobado, rechazado, ....
   */
  public void setEstado(EstadoHechoDinamico estado) {
    this.estado = estado;
  }

}
