package ar.edu.utn.frba.dds;

import static org.junit.jupiter.api.Assertions.*;

import ar.edu.utn.frba.dds.consensuador.Consensuador;
import ar.edu.utn.frba.dds.consensuador.CriterioConsenso;
import ar.edu.utn.frba.dds.hecho.Hecho;
import ar.edu.utn.frba.dds.hecho.OrigenHecho;
import java.time.LocalDateTime;
import java.util.List;
import java.util.EnumSet;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class ConsensuadorTest {
  private Hecho hecho(String origen) {
    return new Hecho.Builder()
        .titulo("Incendio")
        .descripcion("Descripcion del incendio2")
        .categoria("UnaCategoria")
        .lugar("501","502", "Buenos Aires")
        .fechaAcontecimiento(LocalDateTime.now())
        .origen(OrigenHecho.valueOf(origen))
        .build();
  }

  @BeforeEach
  void resetSingleton() throws Exception {
    var field = Consensuador.class.getDeclaredField("instancia");
    field.setAccessible(true);
    field.set(null, null);
  }

  @Test
  void excepcionSiNoInicializado() {
    IllegalStateException ex = assertThrows(IllegalStateException.class, () -> Consensuador.instancia());
    assertEquals("Consensuador no fue inicializado", ex.getMessage());
  }

  @Test
  void inicializarYObtenerInstancia() {
    Consensuador.inicializar(List.of(CriterioConsenso.ABSOLUTO));
    assertNotNull(Consensuador.instancia());
  }

  @Test
  void procesarConsensosAplicaTodosLosCriterios() {

    Hecho h1 = hecho("DATASET");
    Hecho h2 = hecho("CONTRIBUYENTE");
    Hecho h3 = hecho("CARGA_MANUAL");
    List<Hecho> hechos = List.of(h1, h2, h3);

    Consensuador.inicializar(List.of(
        CriterioConsenso.ABSOLUTO,
        CriterioConsenso.MAYORIA_SIMPLE,
        CriterioConsenso.MULTIPLES_MENCIONES
    ));

    Map<String, EnumSet<CriterioConsenso>> resultado = Consensuador
        .instancia()
        .procesarConsensos(hechos, 3);

    EnumSet<CriterioConsenso> criterios = resultado.get("incendio");
    assertNotNull(criterios);
    assertTrue(criterios.contains(CriterioConsenso.ABSOLUTO));
    assertTrue(criterios.contains(CriterioConsenso.MAYORIA_SIMPLE));
    assertTrue(criterios.contains(CriterioConsenso.MULTIPLES_MENCIONES));
  }

  @Test
  void noAplicaCriteriosConUnSoloHecho() {
    Hecho h1 = hecho("DATASET");
    List<Hecho> hechos = List.of(h1);

    Consensuador.inicializar(List.of(
        CriterioConsenso.ABSOLUTO,
        CriterioConsenso.MAYORIA_SIMPLE,
        CriterioConsenso.MULTIPLES_MENCIONES
    ));

    Map<String, EnumSet<CriterioConsenso>> resultado = Consensuador.instancia()
        .procesarConsensos(hechos, 1);

    EnumSet<CriterioConsenso> criterios = resultado.get("incendio");
    assertNotNull(criterios);
    assertTrue(criterios.isEmpty());
  }

  @Test
  void testNoSeAplicaConsensoAbsolutoSiNoSeAlcanzaElMinimo() {
    Hecho h1 = hecho("DATASET");
    Hecho h2 = hecho("CARGA_MANUAL");

    Consensuador.inicializar(List.of(
        CriterioConsenso.ABSOLUTO
    ));

    Map<String, EnumSet<CriterioConsenso>> resultado = Consensuador.instancia()
        .procesarConsensos(List.of(h1, h2), 3); // mínimo: 3

    EnumSet<CriterioConsenso> criterios = resultado.get("incendio");
    assertNotNull(criterios);
    assertTrue(criterios.isEmpty());
  }



}