package ar.edu.utn.frba.dds.hecho;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
@Table(name = "Lugares")
@Entity
public class Lugar {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String latitud;
  private String longitud;
  private String provincia;

  public Lugar(String latitud, String longitud, String provincia) {
    this.latitud = latitud;
    this.longitud = longitud;
    this.provincia = provincia;
  }
  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Lugar)) return false;
    Lugar otro = (Lugar) o;
    return Objects.equals(latitud, otro.latitud) &&
        Objects.equals(longitud, otro.longitud);
  }

  @Override
  public int hashCode() {
    return Objects.hash(latitud, longitud);
  }

}
