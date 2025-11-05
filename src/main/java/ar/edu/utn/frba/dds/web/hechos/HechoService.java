package ar.edu.utn.frba.dds.web.hechos;

import ar.edu.utn.frba.dds.coleccion.Coleccion;
import ar.edu.utn.frba.dds.fuente.dinamica.FuenteDinamica;
import ar.edu.utn.frba.dds.fuente.dinamica.hechos_dinamicos.HechoDinamico;
import ar.edu.utn.frba.dds.hecho.Lugar;
import ar.edu.utn.frba.dds.web.colecciones.ColeccionService;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class HechoService {

  private final ColeccionService coleccionService;

  public HechoService(ColeccionService coleccionService) {
    this.coleccionService = coleccionService;
  }

  public void registrarHechoDinamico(Long coleccionId, String titulo, String descripcion,
                                     String categoria, String latitud, String longitud,
                                     String provincia, String fecha, String usuario) {
    Coleccion coleccion = coleccionService.buscarPorId(coleccionId)
        .orElseThrow(() -> new IllegalArgumentException("Colección no encontrada"));

    if (!(coleccion.getFuente() instanceof FuenteDinamica fuenteDinamica)) {
      throw new IllegalStateException("La colección no está respaldada por una fuente dinámica");
    }

    LocalDateTime fechaHecho = parsearFecha(fecha);
    HechoDinamico hecho = new HechoDinamico(
        titulo,
        descripcion,
        categoria,
        new Lugar(latitud, longitud, provincia),
        fechaHecho,
        usuario
    );

    fuenteDinamica.guardarSolicitudHecho(hecho);
  }

  private LocalDateTime parsearFecha(String fecha) {
    if (fecha == null || fecha.isBlank()) {
      throw new IllegalArgumentException("Debe indicar la fecha del hecho");
    }
    try {
      return LocalDateTime.parse(fecha, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    } catch (DateTimeParseException e) {
      return LocalDateTime.parse(fecha + ":00", DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
  }
}
