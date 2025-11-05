package ar.edu.utn.frba.dds.estadistica;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import lombok.Getter;

@Getter
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
}