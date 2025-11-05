package ar.edu.utn.frba.dds;

import static org.junit.jupiter.api.Assertions.*;

import ar.edu.utn.frba.dds.filtros.FiltroHechos;
import ar.edu.utn.frba.dds.fuente.estatica.LectorCsv;
import ar.edu.utn.frba.dds.fuente.estatica.Linea;
import ar.edu.utn.frba.dds.hecho.Hecho;
import ar.edu.utn.frba.dds.hecho.Lugar;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class FuenteEstaticaTest {

  /*
  @Test
  @DisplayName("Filtro por título coincidente")
  public void filtroPorTituloCoincide() {
    FiltroHechos filtro = new FiltroHechos.Builder()
        .titulo("Inundacion Bahia Blanca")
        .build();
    assertTrue(filtro.aplicarFiltro(hechoInundacion()));
  }

  @Test
  @DisplayName("Filtro por descripción que contiene texto")
  public void filtroPorDescripcionCoincide() {
    FiltroHechos filtro = new FiltroHechos.Builder()
        .descripcion("temporal que afectó al partido de Bahía Blanca")
        .build();
    assertTrue(filtro.aplicarFiltro(hechoInundacion()));
  }
  
  @Test
  @DisplayName("Filtro por categoría coincidente")
  public void filtroPorCategoriaCoincide() {
    FiltroHechos filtro = new FiltroHechos.Builder()
        .categoria("Inundacion Urbana")
        .build();
    assertTrue(filtro.aplicarFiltro(hechoInundacion()));
  }

  @Test
  @DisplayName("Filtro por latitud coincidente")
  public void filtroPorLatitudCoincide() {
    FiltroHechos filtro = new FiltroHechos.Builder()
        .latitud("-38.7176")
        .build();
    assertTrue(filtro.aplicarFiltro(hechoInundacion()));
  }

  @Test
  @DisplayName("Filtro por longitud coincidente")
  public void filtroPorLongitudCoincide() {
    FiltroHechos filtro = new FiltroHechos.Builder()
        .longitud("-62.26545")
        .build();
    assertTrue(filtro.aplicarFiltro(hechoInundacion()));
  }

  @Test
  @DisplayName("Filtro por fecha del hecho coincidente")
  public void filtroPorFechaHechoCoincide() {
    FiltroHechos filtro = new FiltroHechos.Builder()
        .fechaHecho(LocalDate.of(2025, 3, 7))
        .build();
    assertTrue(filtro.aplicarFiltro(hechoInundacion()));
  }


  @Test
  @DisplayName("Filtro por título no coincidente")
  public void filtroPorTituloNoCoincide() {
    FiltroHechos filtro = new FiltroHechos.Builder()
        .titulo("Incendio en el Bolson")
        .build();
    assertFalse(filtro.aplicarFiltro(hechoInundacion()));
  }


  @Test
  @DisplayName("Filtro por descripción: No contiene texto")
  public void filtroPorDescripcionNoCoincide() {
    FiltroHechos filtro = new FiltroHechos.Builder()
        .descripcion("esta descripción no aparece")
        .build();
    assertFalse(filtro.aplicarFiltro(hechoInundacion()));
  }


  @Test
  @DisplayName("Filtro por categoría no coincidente")
  public void filtroPorCategoriaNoCoincide() {
    FiltroHechos filtro = new FiltroHechos.Builder()
        .categoria("Catástrofe Ambiental")
        .build();
    assertFalse(filtro.aplicarFiltro(hechoInundacion()));
  }

  @Test
  @DisplayName("Filtro por latitud no coincidente")
  public void filtroPorLatitudNoCoincide() {
    FiltroHechos filtro = new FiltroHechos.Builder()
        .latitud("-40.0000")
        .build();
    assertFalse(filtro.aplicarFiltro(hechoInundacion()));
  }

  @Test
  @DisplayName("Filtro por longitud no coincidente")
  public void filtroPorLongitudNoCoincide() {
    FiltroHechos filtro = new FiltroHechos.Builder()
        .longitud("-60.0000")
        .build();
    assertFalse(filtro.aplicarFiltro(hechoInundacion()));
  }

  @Test
  @DisplayName("Filtro por fecha del hecho no coincidente")
  public void filtroPorFechaHechoNoCoincide() {
    FiltroHechos filtro = new FiltroHechos.Builder()
        .fechaHecho(LocalDate.of(2024, 12, 31))
        .build();
    assertFalse(filtro.aplicarFiltro(hechoInundacion()));
  }
*/
  private Hecho hechoInundacion(){
    String titulo = "Inundacion Bahia Blanca";
    String descripcion = "La inundación de Bahía Blanca de 2025 fue un desastre natural " +
        "provocado por un temporal que afectó al partido de Bahía Blanca, ubicado al sur " +
        "de la Provincia de Buenos Aires, Argentina.[";
    String categoria = "Inundacion Urbana";
    String latitud = "-38.7176";
    String longitud = "-62.26545";
    String provincia = "Buenos Aires";
    Lugar lugar = new Lugar(latitud, longitud, provincia);
    LocalDateTime fechaAcontecimiento = LocalDateTime.of(2025, 03, 07, 0, 0);

    return new Hecho.Builder()
        .titulo(titulo)
        .descripcion(descripcion)
        .categoria(categoria)
        .lugar(latitud, longitud, provincia)
        .fechaAcontecimiento(fechaAcontecimiento)
        .build();
  }


  // Dummy para simular un lector que devuelve una lista de líneas en orden
  class LectorCsvDummy extends LectorCsv {
    private final List<Linea> lineas;
    private int index = 0;

    public LectorCsvDummy(){
      super("LectorCsvDummy");
      lineas = new ArrayList<>();
    }

    @Override
    public Linea leerLinea(){
      return null;
    }
    @Override
    public void cerrarLector() {}

    @Override
    public void reiniciar() {

    }


  }

  class LectorCsvMock extends LectorCsv {
    private List<Linea> lineas = new ArrayList<>();
    private int indiceActual = 0;

    public LectorCsvMock(){
      super("LectorCsvDummy");
      lineas = new ArrayList<>();
    }

    public void addLinea(Linea linea) {
      lineas.add(linea);
    }

    @Override
    public Linea leerLinea() {
      if (indiceActual < lineas.size()) {
        return lineas.get(indiceActual++);
      }
      return null; // fin de la lectura
    }

    @Override
    public void cerrarLector() {}

    @Override
    public void reiniciar() {

    }

  }

  /*
  private Linea lineaIncendio(){
    String titulo = "Incendio en el Bolson ";
    String descripcion = "En la Patagonia argentina, los incendios forestales están " +
        "causando una gran devastación, habiéndose quemado ya más de 150,000 hectáreas desde" +
        " principios de 2025";
    String categoria = "Incendio Forestal";
    String latitud = "-41.96667";
    String longitud = "-71.51667";;
    LocalDate fechaDelHecho = LocalDate.of(2024, 01, 31);

    return new Linea(titulo, descripcion, categoria, latitud, longitud, fechaDelHecho, DATASET);
  }

  private FiltroHechos filtroTituloInundacionBahiaBlanca(){
    return new FiltroHechos.Builder()
        .titulo("Inundacion Bahia Blanca")
        .origen(DATASET)  // debe coincidir con un valor del enum Origen
        .build();
  }

  private FiltroHechos filtroTituloBasuralUrbano(){
    return new FiltroHechos.Builder()
        .titulo("Basural Urbano")
        .origen(DATASET)  // debe coincidir con un valor del enum Origen
        .build();
  }

  private FiltroHechos filtroVacio(){
    return new FiltroHechos.Builder()
        .build();
  }

  private FiltroHechos filtroCategoriaIncendioForestal(){
    return new FiltroHechos.Builder()
        .categoria("Incendio Forestal")
        .origen(DATASET)  // debe coincidir con un valor del enum Origen
        .build();
  }
*/

}
