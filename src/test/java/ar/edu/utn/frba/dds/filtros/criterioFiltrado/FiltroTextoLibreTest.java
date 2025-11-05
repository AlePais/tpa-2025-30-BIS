package ar.edu.utn.frba.dds.filtros.criterioFiltrado;

import ar.edu.utn.frba.dds.hecho.Hecho;
import ar.edu.utn.frba.dds.hecho.Lugar;
import ar.edu.utn.frba.dds.hecho.OrigenHecho;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FiltroTextoLibreTest {

  @Test
  void aplicaFiltradoSobreTituloYDescripcion() {
    Hecho coincidente = crearHecho("Denuncia ambiental", "Vertido clandestino en el río");
    Hecho noCoincidente = crearHecho("Reforestación comunitaria", "Campaña de árboles nativos");

    FiltroTextoLibre filtro = new FiltroTextoLibre("río clandestino");

    Assertions.assertTrue(filtro.aplicar(coincidente));
    Assertions.assertFalse(filtro.aplicar(noCoincidente));
  }

  private Hecho crearHecho(String titulo, String descripcion) {
    Hecho hecho = new Hecho();
    hecho.setTitulo(titulo);
    hecho.setDescripcion(descripcion);
    hecho.setCategoria("AMBIENTAL");
    hecho.setFechaAcontecimiento(LocalDateTime.now());
    hecho.setLugar(new Lugar("-34.0", "-58.0", "Buenos Aires"));
    hecho.setOrigen(OrigenHecho.DATASET);
    return hecho;
  }
}
