package ar.edu.utn.frba.dds.fuente.dinamica;

import ar.edu.utn.frba.dds.fuente.Fuente;
import ar.edu.utn.frba.dds.hecho.Hecho;
import ar.edu.utn.frba.dds.fuente.dinamica.hechos_dinamicos.HechoDinamico;

import java.util.List;
import javax.persistence.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

@Entity
@DiscriminatorValue("DINAMICA")
public class FuenteDinamica extends Fuente {
  @Transient
  private static final Log log = LogFactory.getLog(FuenteDinamica.class);
  @Transient
  private RepositorioHechosySolicitudes repoHechosYSolicitudes;

  public FuenteDinamica() {
    this(RepositorioHechosySolicitudes.getInstancia());
  }

  public FuenteDinamica(RepositorioHechosySolicitudes repoHechosYSolicitudes) {
    this.repoHechosYSolicitudes = repoHechosYSolicitudes;
  }

  /**
    llega una solocitud dinamica y la guarda
  */
  public void guardarSolicitudHecho(HechoDinamico hecho){

    if (hecho != null) {
      obtenerRepositorio().agregarHecho(hecho);
    }
  }

  /**
    obtiene todos los hechos aprobados y aprobados con sugerencia del Repositorio
  */
  @Override
  public List<Hecho> obtenerHechos(){
    List<HechoDinamico> hechosDinamicos = obtenerRepositorio().obtenerHechos();

    return hechosDinamicos.stream()
        .map(HechoDinamico::convertirAHecho)
        .toList();
  }

  /**
    obtiene todos los hechos pendientes
  */
  public List<HechoDinamico> obtenerHechosPendientes() {
    return repoHechosYSolicitudes.obtenerSolicitudes();
  }

  private RepositorioHechosySolicitudes obtenerRepositorio() {
    if (this.repoHechosYSolicitudes == null) {
      this.repoHechosYSolicitudes = RepositorioHechosySolicitudes.getInstancia();
    }
    return this.repoHechosYSolicitudes;
  }
}
