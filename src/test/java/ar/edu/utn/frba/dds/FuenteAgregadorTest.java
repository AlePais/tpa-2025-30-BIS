package ar.edu.utn.frba.dds;

import static org.junit.jupiter.api.Assertions.*;

import ar.edu.utn.frba.dds.consensuador.Consensuador;
import ar.edu.utn.frba.dds.consensuador.CriterioConsenso;
import ar.edu.utn.frba.dds.fuente.Fuente;
import ar.edu.utn.frba.dds.fuente.agregador.FuenteAgregador;
import ar.edu.utn.frba.dds.hecho.Hecho;
import ar.edu.utn.frba.dds.hecho.OrigenHecho;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FuenteAgregadorTest {

  private FuenteAgregador agregador;

  private Hecho crearHecho(String titulo) {
    return crearHecho(titulo, "desc");
  }

  private Hecho crearHecho(String titulo, String descripcion) {
    return new Hecho.Builder()
        .titulo(titulo)
        .descripcion(descripcion)
        .categoria("cat")
        .lugar("10", "20", "Buenos Aires")
        .fechaAcontecimiento(LocalDateTime.of(2024, 7, 4, 0, 0))
        .origen(OrigenHecho.CONTRIBUYENTE)
        .build();
  }

  private Fuente crearFuente(Hecho hecho){
    return new Fuente() {
      @Override
      public List<Hecho> obtenerHechos() {
        return List.of(hecho);
      }
    };
  }

  @BeforeEach
  void setUp() throws Exception {
    var field = Consensuador.class.getDeclaredField("instancia");
    field.setAccessible(true);
    field.set(null, null);

    List<CriterioConsenso> criterios = List.of(
        CriterioConsenso.ABSOLUTO,
        CriterioConsenso.MAYORIA_SIMPLE,
        CriterioConsenso.MULTIPLES_MENCIONES
    );
    Consensuador.inicializar(criterios);
    agregador = new FuenteAgregador();
  }

  @Test
  void consultarFuentesAgregaHechosAlRepositorio() {
    Hecho hecho = crearHecho("Hecho1");
    Fuente fuenteMock = crearFuente(hecho);
    agregador.agregarFuente(fuenteMock);

    agregador.consultarFuentes();

    List<Hecho> hechos = agregador.obtenerHechos();
    assertEquals(1, hechos.size());
    assertEquals("Hecho1", hechos.get(0).getTitulo());
  }

  @Test
  void calcularConsensosAgregaCriteriosSiCorresponde() {

    Hecho hecho = crearHecho("Hecho1");
    Fuente fuente1 = crearFuente(hecho);
    Fuente fuente2 = crearFuente(crearHecho("Hecho1"));
    Fuente fuente3 = crearFuente(crearHecho("Hecho1"));

    agregador.agregarFuente(fuente1);
    agregador.agregarFuente(fuente2);
    agregador.agregarFuente(fuente3);

    agregador.consultarFuentes();
    agregador.calcularConsensos();

    Hecho hechoResultado = agregador.obtenerHechos().get(0);
    assertTrue(hechoResultado.cumpleCriterio(CriterioConsenso.ABSOLUTO));
    assertTrue(hechoResultado.cumpleCriterio(CriterioConsenso.MAYORIA_SIMPLE));
    assertTrue(hechoResultado.cumpleCriterio(CriterioConsenso.MULTIPLES_MENCIONES));
  }

  @Test
  void calcularConsensosNoAgregaNadaSinFuentes() {
    agregador.consultarFuentes();
    agregador.calcularConsensos();

    assertTrue(agregador.obtenerHechos().isEmpty());
  }

  @Test
  void conservaVersionesConflictoParaNavegacionIrrestricta() {
    Hecho original = crearHecho("Hecho1", "desc uno");
    Hecho variante = crearHecho("Hecho1", "desc dos");

    agregador.agregarFuente(crearFuente(original));
    agregador.agregarFuente(crearFuente(variante));

    agregador.consultarFuentes();

    List<Hecho> hechos = agregador.obtenerHechos();
    assertEquals(2, hechos.size());
  }
}
