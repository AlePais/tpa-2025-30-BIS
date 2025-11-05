package ar.edu.utn.frba.dds.fuente.dinamica;
import ar.edu.utn.frba.dds.fuente.dinamica.hechos_dinamicos.EstadoHechoDinamico;
import ar.edu.utn.frba.dds.fuente.dinamica.hechos_dinamicos.HechoDinamico;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

import java.util.List;

public class RepositorioHechosySolicitudes implements WithSimplePersistenceUnit{
  private static final RepositorioHechosySolicitudes INSTANCIA = new RepositorioHechosySolicitudes();

  public static RepositorioHechosySolicitudes getInstancia() {
    return INSTANCIA;
  }

  public void agregarHecho(HechoDinamico hecho){// toma hecho y hechoDinamico
    if (hecho == null) {
      return;
    }
    withTransaction(() -> {
      entityManager().persist(hecho);
      return null;
    });
  }

  public List<HechoDinamico> obtenerHechos(){// toma hecho y hechoDinamico
    return entityManager()
        .createQuery("FROM HechoDinamico h WHERE h.estado IN :estados", HechoDinamico.class)
        .setParameter("estados", List.of(EstadoHechoDinamico.APROBADO,
            EstadoHechoDinamico.APROBADO_CON_SUGERENCIA))
        .getResultList();

  }

  public List<HechoDinamico> obtenerSolicitudes(){
      return entityManager().createQuery("FROM HechoDinamico h WHERE h.estado = :pendiente", HechoDinamico.class)
          .setParameter("pendiente", EstadoHechoDinamico.PENDIENTE)
          .getResultList();
  }

  public void aprobarSolicitud(HechoDinamico hechoDinamico) {
    if (hechoDinamico == null) {
      return;
    }
    withTransaction(() -> {
      hechoDinamico.aprobar();
      entityManager().merge(hechoDinamico);
      return null;
    });
  }

  public void cambiarEstado(HechoDinamico hecho, EstadoHechoDinamico nuevoEstado) {
    if (hecho == null || nuevoEstado == null) {
      return;
    }
    withTransaction(() -> {
      hecho.setEstado(nuevoEstado);
      entityManager().merge(hecho);
      return null;
    });
  }

  public List<HechoDinamico> obtenerHechosDinamicosPorEstado(EstadoHechoDinamico estado) {

    return entityManager()
        .createQuery("from HechoDinamico where estado = :estado", HechoDinamico.class)
        .setParameter("estado", estado)
        .getResultList();
  }

  public void reset() {
    withTransaction(() -> {
      entityManager().createQuery("DELETE FROM HechoDinamico").executeUpdate();
      return null;
    });
  }
}

//  /**
//   agrega un hecho
//   */
//  public void agregarHecho(HechoDinamico hecho){
//    hechosDinamicos.add(hecho);
//  }
//
//  /**
//    obtiene todos los hechos aprobados y aprobados con sugerencia
//  */
//  public List<HechoDinamico> obtenerHechos(){
//    return obtenerHechosPorEstado(EstadoHechoDinamico.APROBADO, EstadoHechoDinamico.APROBADO_CON_SUGERENCIA);
//  }
//
//  /**
//   obtiene todos las solicitudes pendientes
//   */
//  public List<HechoDinamico> obtenerSolicitudes(){
//    return obtenerHechosPorEstado(EstadoHechoDinamico.PENDIENTE);
//  }
//
//  public void exportarHechosDinamicosAJson() {
//    String rutaArchivo = "src/main/resources/bk_hechos_dinamicos.json";
//    ObjectMapper mapper = new ObjectMapper();
//    mapper.registerModule(new JavaTimeModule());
//    mapper.enable(SerializationFeature.INDENT_OUTPUT);
//
//    try {
//      mapper.writeValue(new File(rutaArchivo), this.hechosDinamicos);
//    } catch (IOException e) {
//      throw new RuntimeException("Error al exportar los hechos dinámicos a JSON", e);
//    }
//  }
//
//
//  //------- FUNCIONES AUXILIARES -------
//  public List<HechoDinamico> obtenerHechosPorEstado(EstadoHechoDinamico estado) {
//    return this.hechosDinamicos.stream().filter(hecho -> hecho.getEstado() == estado).toList();
//  }
//
//  public List<HechoDinamico> obtenerHechosPorEstado(EstadoHechoDinamico estado1, EstadoHechoDinamico estado2) {
//    return this.hechosDinamicos.stream()
//        .filter(hecho -> hecho.getEstado() == estado1 || hecho.getEstado() == estado2).toList();
//  }
//
//  //------- Solo para tests -------
//  public void reset() {
//    this.hechosDinamicos.clear();
//  }
//}
//