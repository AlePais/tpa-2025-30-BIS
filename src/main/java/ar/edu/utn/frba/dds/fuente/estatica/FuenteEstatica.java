package ar.edu.utn.frba.dds.fuente.estatica;

import ar.edu.utn.frba.dds.hecho.Hecho;
import ar.edu.utn.frba.dds.fuente.Fuente;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.*;

@Entity
@DiscriminatorValue("ESTATICA")
public class FuenteEstatica extends Fuente {

  @Transient
  private LectorCsv lector;
  @Column(name = "path_archivo")
  private String path;

  public FuenteEstatica() {
    // necesario para JPA
  }

  public FuenteEstatica(String path) {
    this.path = path;
    this.initLector();
  }

  @PostLoad
  public void initLector() {
    this.lector = new LectorCsv(path);
  }

  @Override
  public List<Hecho> obtenerHechos() {
    Hecho hecho;
    List<Hecho> hechos = new ArrayList<>();
    lector.reiniciar();

    Linea linea = lector.leerLinea();
    while (linea != null) {
      hecho = new Hecho.Builder()
          .titulo(linea.getTitulo())
          .descripcion(linea.getDescripcion())
          .categoria(linea.getCategoria())
          .lugar(linea.getLatitud(), linea.getLongitud(), linea.getProvincia())
          .fechaAcontecimiento(linea.getFechaHecho())
          .build();
      hechos.add(hecho);
      linea = lector.leerLinea();

    }

    lector.cerrarLector();

    return hechos;
  }
}
