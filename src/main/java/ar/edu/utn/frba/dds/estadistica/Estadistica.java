package ar.edu.utn.frba.dds.estadistica;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
@Entity
@Table(name = "Estadisticas")
public class Estadistica {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  private TipoEstadistica tipo;

  private String clave; // Ej: provincia, categoría, hora, etc.
  private Long valor;

  private LocalDateTime fechaCalculo = LocalDateTime.now();

  public Estadistica() {}

  public Estadistica(TipoEstadistica tipo, String clave, Long valor) {
    this.tipo = tipo;
    this.clave = clave;
    this.valor = valor;
    this.fechaCalculo = LocalDateTime.now();
  }

  public Long getId() {
    return id;
  }

  public TipoEstadistica getTipo() {
    return tipo;
  }

  public void setTipo(TipoEstadistica tipo) {
    this.tipo = tipo;
  }

  public String getClave() {
    return clave;
  }

  public void setClave(String clave) {
    this.clave = clave;
  }

  public Long getValor() {
    return valor;
  }

  public void setValor(Long valor) {
    this.valor = valor;
  }

  public LocalDateTime getFechaCalculo() {
    return fechaCalculo;
  }

  public void setFechaCalculo(LocalDateTime fechaCalculo) {
    this.fechaCalculo = fechaCalculo;
  }
}