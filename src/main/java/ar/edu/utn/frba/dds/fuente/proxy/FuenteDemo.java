package ar.edu.utn.frba.dds.fuente.proxy;

import ar.edu.utn.frba.dds.fuente.Fuente;
import ar.edu.utn.frba.dds.hecho.Hecho;
import ar.edu.utn.frba.dds.hecho.OrigenHecho;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.*;
import javax.persistence.*;
//import java.util.concurrent.*;

@Entity
@DiscriminatorValue("DEMO")
public class FuenteDemo extends Fuente {
  @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinTable(
      name = "fuente_demo_hechos"
  )
  private List<Hecho> hechos = new ArrayList<>(); //"contiene todos los hechos" se actualizan cada hora
  private LocalDateTime ultimaFechaConsultada; // ultima fecha_hora en la que se actualizo
  @Transient
  private Conexion conexion;
  @Transient
  private URL url;
  private String urlString;
  //private ScheduledExecutorService scheduler;

  public FuenteDemo() {
    // JPA necesita constructor vacío
  }

  public FuenteDemo(Conexion conexion, URL url) {
    this.conexion = conexion;
    this.url = url;
    this.urlString = url.toString();
    //this.scheduler = Executors.newSingleThreadScheduledExecutor();
    //this.iniciarCronInterno();
  }

  @PostLoad
  public void initURL() {
    try {
      this.url = new URL(this.urlString);
    } catch (Exception e) {
      throw new RuntimeException("URL inválida");
    }
  }

  public List<Hecho> obtenerHechos() {
    if (conexion == null) {
      throw new IllegalStateException("La conexión de la fuente demo no está inicializada");
    }

    if (debeActualizar()) {
      consultarHechosNuevos();
      actualizarFechaConsultada();
    }

    limpiarHechosExpirados();
    return List.copyOf(hechos);
  }

  public void consultarHechosNuevos() {
    Map<String, Object> datos;

    while ((datos = conexion.siguienteHecho(this.url, this.ultimaFechaConsultada)) != null) {
      Hecho hecho = convertirAHecho(datos);
      hechos.add(hecho);
    }
  }

  public void actualizarFechaConsultada() {
    this.ultimaFechaConsultada = LocalDateTime.now();
  }

  //private void iniciarCronInterno() {

  //  scheduler.scheduleAtFixedRate(() -> {
  //    consultarHechosNuevos(); //primer metodo a ejecutar
  //    actualizarFechaConsultada(); //segundo metodo a ejecutar
  //  }, 0, 5, TimeUnit.SECONDS); //Simulamos que 1 hora es = 5 segundos y ejecuta estos dos metodos
  //}

  //public void cerrarCronInterno() {
  //  scheduler.shutdown();
  //}

  // --------  FUNCIONES AUXILIARES --------
  public Hecho convertirAHecho(Map<String, Object> datos) {

    return new Hecho.Builder()
        .titulo((String) datos.get("titulo"))
        .descripcion((String) datos.get("descripcion"))
        .categoria((String) datos.get("categoria"))
        .lugar((String) datos.get("latitud"),(String) datos.get("longitud"), (String) datos.get("provincia"))
        .fechaAcontecimiento((LocalDateTime) datos.get("fecha"))
        .fechaCarga(LocalDateTime.now())
        .origen(OrigenHecho.PROXY)
        .build();
  }

  private boolean debeActualizar() {
    if (this.ultimaFechaConsultada == null) {
      return true;
    }
    return this.ultimaFechaConsultada.isBefore(LocalDateTime.now().minusHours(1));
  }

  private void limpiarHechosExpirados() {
    LocalDateTime limite = LocalDateTime.now().minusHours(1);
    hechos.removeIf(hecho -> hecho.getFechaCarga() != null
        && hecho.getFechaCarga().isBefore(limite));
  }
}
