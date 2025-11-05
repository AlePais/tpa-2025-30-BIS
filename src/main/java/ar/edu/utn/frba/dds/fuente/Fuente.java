package ar.edu.utn.frba.dds.fuente;

import ar.edu.utn.frba.dds.hecho.Hecho;
import javax.persistence.*;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "tipo_fuente", discriminatorType = DiscriminatorType.STRING)
public abstract class Fuente {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  public abstract List<Hecho> obtenerHechos();
}
