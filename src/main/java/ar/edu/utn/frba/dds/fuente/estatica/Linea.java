package ar.edu.utn.frba.dds.fuente.estatica;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import java.time.LocalDate;
import ar.edu.utn.frba.dds.hecho.OrigenHecho;
import java.time.LocalDateTime;

public class Linea {
  @CsvBindByName(column = "Titulo")
  private String titulo;
  @CsvBindByName(column = "descripcion")
  private String descripcion;
  @CsvBindByName(column = "Categoria")
  private String categoria;
  @CsvBindByName(column = "Latitud")
  private String latitud;
  @CsvBindByName(column = "Longitud")
  private String longitud;
  @CsvBindByName(column = "Provincia")
  private String provincia;
  @CsvBindByName(column = "fecha")
  @CsvDate("yyyy-MM-dd")
  private LocalDateTime fechaHecho;
  private OrigenHecho origen;

  public Linea() {
  }

  public Linea(String titulo, String descripcion, String categoria, String latitud,
               String longitud, String provincia, LocalDateTime fechaHecho, OrigenHecho origen) {
    this.titulo = titulo;
    this.descripcion = descripcion;
    this.categoria = categoria;
    this.latitud = latitud;
    this.longitud = longitud;
    this.provincia = provincia;
    this.fechaHecho = fechaHecho;
    this.origen = origen;
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

  public String getCategoria() {
    return categoria;
  }

  public void setCategoria(String categoria) {
    this.categoria = categoria;
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

  public LocalDateTime getFechaHecho() {
    return fechaHecho;
  }

  public void setFechaHecho(LocalDateTime fechaHecho) {
    this.fechaHecho = fechaHecho;
  }

  public OrigenHecho getOrigen() {
    return origen;
  }

  public void setOrigen(OrigenHecho origen) {
    this.origen = origen;
  }
}
