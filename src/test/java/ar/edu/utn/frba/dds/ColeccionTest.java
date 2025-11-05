package ar.edu.utn.frba.dds;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import ar.edu.utn.frba.dds.coleccion.Coleccion;
import ar.edu.utn.frba.dds.consensuador.CriterioConsenso;
import ar.edu.utn.frba.dds.filtros.AtributoFiltro;
import ar.edu.utn.frba.dds.filtros.FiltroHechos;
import ar.edu.utn.frba.dds.filtros.ParametroFiltro;
import ar.edu.utn.frba.dds.fuente.Fuente;
import ar.edu.utn.frba.dds.gestor_solicitudes_eliminacion.GestorSolicitudesEliminacion;
import ar.edu.utn.frba.dds.gestor_solicitudes_eliminacion.SolicitudEliminacion;
import ar.edu.utn.frba.dds.hecho.Hecho;
import ar.edu.utn.frba.dds.hecho.OrigenHecho;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ColeccionTest {

  private static class FuenteFake extends Fuente {
    private final List<Hecho> hechos;

    FuenteFake(List<Hecho> hechos) {
      this.hechos = hechos;
    }

    public List<Hecho> obtenerHechos() { return hechos; }
  }

  private Hecho hecho( String titulo, String categoria) {
    return new Hecho.Builder()
                    .titulo(titulo)
                    .descripcion("descripcion")
                    .categoria(categoria)
                    .lugar("56","090","Buenos Aires")
                    .fechaAcontecimiento(LocalDateTime.now())
                    .origen(OrigenHecho.DATASET)
                    .build();
  }

  private String motivoCon500Caracteres() {
    return "X".repeat(500);
  }

  @BeforeEach
  void limpiarGestor() {
    GestorSolicitudesEliminacion.getInstancia().reset();
  }

  @Test
  void testObtenerHechosSoloPertinentesYNoEliminados() {
  //TODO solo verifica pertinentes, no los eliminados
    Hecho a1 = hecho("Titulo1","CatA");
    Hecho a2 = hecho("Titulo2","CatA");
    Hecho b1 = hecho("Titulo3","CatB");

    /* SolicitudEliminacion sol = new SolicitudEliminacion("Titulo2", motivoCon500Caracteres());
    GestorSolicitudesEliminacion.getInstancia().agregarSolicitud(sol);
    GestorSolicitudesEliminacion.getInstancia().aprobarSolicitud(sol); */

    // Fuente ejemplo
    Fuente fuente = new FuenteFake(List.of(a1,a2,b1));
    ParametroFiltro parametroFiltro = new ParametroFiltro(AtributoFiltro.CATEGORIA, "CatA");

    Coleccion coleccion = new Coleccion("Coleccion1","Descripcion","coleccion1",fuente,
        null);

    coleccion.agregarCriterioPertenencia(parametroFiltro);

    List<Hecho> resultado = coleccion.obtenerHechos();

    assertEquals(2, resultado.size());
    assertEquals("Titulo1", resultado.get(0).getTitulo());
    assertEquals("Titulo2", resultado.get(1).getTitulo());
  }

  @Test
  void testHechoCoincideConFiltroPeroFueEliminadoNoSeDevuelve() {
    //TODO comentado ya que no existe verificadordeeliminados

    Hecho h1 = hecho("TituloCoincide", "CategoriaA");
    /*
    SolicitudEliminacion solicitud = new SolicitudEliminacion("TituloCoincide", motivoCon500Caracteres());
    GestorSolicitudesEliminacion.getInstancia().agregarSolicitud(solicitud);
    GestorSolicitudesEliminacion.getInstancia().aprobarSolicitud(solicitud);

    Fuente fuente = new FuenteFake(List.of(h1));

    ParametroFiltro parametroFiltro = new ParametroFiltro(AtributoFiltro.CATEGORIA, "CatA");
    FiltroHechos filtroBase = new FiltroHechos.Builder()
                                              .agregarCriterio(parametroFiltro)
                                              .build();
    Coleccion coleccion = new Coleccion("ColeccionEliminado", "Prueba", fuente, filtroBase, null);
    List<Hecho> resultado = coleccion.obtenerHechos();
    assertTrue(resultado.isEmpty(), "No debe devolver hechos eliminados.");*/
  }

  @Test
  void testDevuelveTodosLosHechosQueCoincidenConElFiltro() {
    Hecho h1 = hecho("Titulo1", "CategoriaA");
    Hecho h2 = hecho("Titulo2", "CategoriaA");
    Hecho h3 = hecho("Titulo3", "CategoriaA");

    Fuente fuente = new FuenteFake(List.of(h1, h2, h3));

    ParametroFiltro parametroFiltro = new ParametroFiltro(AtributoFiltro.CATEGORIA, "CategoriaA");

    Coleccion coleccion = new Coleccion("Coleccion1","Descripcion","coleccion1",fuente,
        null);

    coleccion.agregarCriterioPertenencia(parametroFiltro);

    List<Hecho> resultado = coleccion.obtenerHechos();

    assertEquals(3, resultado.size());
    assertTrue(resultado.stream().anyMatch(h -> h.getTitulo().equals("Titulo1")));
    assertTrue(resultado.stream().anyMatch(h -> h.getTitulo().equals("Titulo2")));
    assertTrue(resultado.stream().anyMatch(h -> h.getTitulo().equals("Titulo3")));

  }
}