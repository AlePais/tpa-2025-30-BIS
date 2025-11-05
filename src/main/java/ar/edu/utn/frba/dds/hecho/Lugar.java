package ar.edu.utn.frba.dds.hecho;

import java.util.Objects;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Table(name = "Lugares")
@Entity
public class Lugar {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  private String latitud;
  private String longitud;
  private String provincia;

  public Lugar() {
  }

  public Lugar(String latitud, String longitud, String provincia) {
    this.latitud = latitud;
    this.longitud = longitud;
    this.provincia = provincia;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getLatitud() {
    return latitud;
  }

  public void setLatitud(String latitud) {
    this.latitud = latitud;
  }

  public String getLongitud() {
    return longitud;
  }

  public void setLongitud(String longitud) {
    this.longitud = longitud;
  }

  public String getProvincia() {
    return provincia;
  }

  public void setProvincia(String provincia) {
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
