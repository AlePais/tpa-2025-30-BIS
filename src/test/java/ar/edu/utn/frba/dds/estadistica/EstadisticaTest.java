package ar.edu.utn.frba.dds.estadistica;

import static org.junit.jupiter.api.Assertions.*;

import ar.edu.utn.frba.dds.estadistica.calculo.HechosPorCategoria;
import ar.edu.utn.frba.dds.estadistica.calculo.HechosPorHora;
import ar.edu.utn.frba.dds.estadistica.calculo.HechosPorProvincia;
import ar.edu.utn.frba.dds.estadistica.calculo.HechosPorProvinciaYCategoria;
import ar.edu.utn.frba.dds.estadistica.calculo.SolicitudesSpam;
import ar.edu.utn.frba.dds.gestor_solicitudes_eliminacion.SolicitudEliminacion;
import ar.edu.utn.frba.dds.hecho.Hecho;
import ar.edu.utn.frba.dds.hecho.Lugar;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;

class EstadisticasTest {

  private Hecho hecho(String desc, String provincia, String categoria, LocalDateTime fecha) {
    Lugar lugar = new Lugar("","",provincia);

    Hecho h = new Hecho();
    h.setDescripcion(desc);
    h.setLugar(lugar);
    h.setCategoria(categoria);
    h.setFechaAcontecimiento(fecha);
    return h;
  }

  private SolicitudEliminacion solicitud(boolean spam) {
    SolicitudEliminacion s = new SolicitudEliminacion();
    s.setSpam(spam);
    return s;
  }

  // --------------------------
  // Hechos por provincia
  // --------------------------
  @Test
  void testHechosPorProvincia_basico() {
    List<Hecho> hechos = List.of(
        hecho("desc1", "BUENOS AIRES", "CatA", LocalDateTime.now()),
        hecho("desc2", "CORDOBA", "CatB", LocalDateTime.now()),
        hecho("desc3", "BUENOS AIRES", "CatC", LocalDateTime.now())
    );

    HechosPorProvincia calc = new HechosPorProvincia();
    List<Estadistica> result = calc.calcular(hechos);

    assertEquals(2, result.size());
    assertTrue(result.stream().anyMatch(e -> e.getClave().equals("BUENOS AIRES") && e.getValor() == 2));
    assertTrue(result.stream().anyMatch(e -> e.getClave().equals("CORDOBA") && e.getValor() == 1));
  }

  @Test
  void testHechosPorProvincia_conProvinciaNull() {
    List<Hecho> hechos = List.of(
        hecho("desc1", null, "CatA", LocalDateTime.now())
    );

    HechosPorProvincia calc = new HechosPorProvincia();
    List<Estadistica> result = calc.calcular(hechos);

    // según tu implementación: ignorar o poner SIN_PROVINCIA
    assertTrue(result.isEmpty() || result.get(0).getClave().equals("SIN_PROVINCIA"));
  }

  // --------------------------
  // Hechos por categoría
  // --------------------------
  @Test
  void testHechosPorCategoria() {
    List<Hecho> hechos = List.of(
        hecho("h1", "BA", "DELITO", LocalDateTime.now()),
        hecho("h2", "BA", "DELITO", LocalDateTime.now()),
        hecho("h3", "BA", "ACCIDENTE", LocalDateTime.now())
    );

    HechosPorCategoria calc = new HechosPorCategoria();
    List<Estadistica> result = calc.calcular(hechos);

    assertEquals(2, result.size());
    assertTrue(result.stream().anyMatch(e -> e.getClave().equals("DELITO") && e.getValor() == 2));
    assertTrue(result.stream().anyMatch(e -> e.getClave().equals("ACCIDENTE") && e.getValor() == 1));
  }

  // --------------------------
  // Hechos por provincia y categoría
  // --------------------------
  @Test
  void testHechosPorProvinciaYCategoria() {
    List<Hecho> hechos = List.of(
        hecho("h1", "BA", "DELITO", LocalDateTime.now()),
        hecho("h2", "BA", "DELITO", LocalDateTime.now()),
        hecho("h3", "BA", "ACCIDENTE", LocalDateTime.now()),
        hecho("h4", "CORDOBA", "DELITO", LocalDateTime.now())
    );

    HechosPorProvinciaYCategoria calc = new HechosPorProvinciaYCategoria();
    List<Estadistica> result = calc.calcular(hechos);

    assertTrue(result.stream().anyMatch(e -> e.getClave().equals("BA|DELITO") && e.getValor() == 2));
    assertTrue(result.stream().anyMatch(e -> e.getClave().equals("BA|ACCIDENTE") && e.getValor() == 1));
    assertTrue(result.stream().anyMatch(e -> e.getClave().equals("CORDOBA|DELITO") && e.getValor() == 1));
  }

  // --------------------------
  // Hechos por hora
  // --------------------------
  @Test
  void testHechosPorHora() {
    LocalDateTime h1 = LocalDateTime.of(2025, 9, 13, 10, 0);
    LocalDateTime h2 = LocalDateTime.of(2025, 9, 13, 10, 30);
    LocalDateTime h3 = LocalDateTime.of(2025, 9, 13, 15, 0);

    List<Hecho> hechos = List.of(
        hecho("desc1", "BA", "Cat", h1),
        hecho("desc2", "BA", "Cat", h2),
        hecho("desc3", "BA", "Cat", h3)
    );

    HechosPorHora calc = new HechosPorHora();
    List<Estadistica> result = calc.calcular(hechos);

    assertTrue(result.stream().anyMatch(e -> e.getClave().equals("10") && e.getValor() == 2));
    assertTrue(result.stream().anyMatch(e -> e.getClave().equals("15") && e.getValor() == 1));
  }

  // --------------------------
  // Solicitudes spam
  // --------------------------
  @Test
  void testSolicitudesSpam() {
    List<Hecho> hechos = List.of();
    List<SolicitudEliminacion> solicitudes = List.of(
        solicitud(true),
        solicitud(false),
        solicitud(true)
    );

    SolicitudesSpam calc = new SolicitudesSpam(solicitudes);
    List<Estadistica> result = calc.calcular(hechos);

    assertEquals(1, result.size());
    assertEquals("Total", result.get(0).getClave());
    assertEquals(2, result.get(0).getValor());
  }
}