package ar.edu.utn.frba.dds;

import ar.edu.utn.frba.dds.fuente.proxy.Conexion;
import ar.edu.utn.frba.dds.hecho.Hecho;
import ar.edu.utn.frba.dds.hecho.Lugar;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class ConexionTest {

  private Conexion conexion;

  @BeforeEach
  public void setup() {
    conexion = new Conexion();
  }

  @Test
  public void testAgregarYRecuperarHecho() throws Exception {
    Hecho hecho = new Hecho.Builder()
        .titulo("Titulo")
        .descripcion("Descripcion")
        .categoria("Categoria")
        .lugar("10.00","20.00","Buenos Aires")
        .fechaAcontecimiento(LocalDateTime.now())
        .build();

    conexion.agregarHecho(hecho);

    URL dummyUrl = new URL("http://dummy.com");
    Map<String, Object> resultado = conexion.siguienteHecho(dummyUrl, LocalDateTime.now());

    assertNotNull(resultado);
    assertEquals("Titulo", resultado.get("titulo"));
  }

  @Test
  public void testSiguienteHechoRetornaNullSiNoHayHechos() throws Exception {
    URL dummyUrl = new URL("http://dummy.com");
    Map<String, Object> resultado = conexion.siguienteHecho(dummyUrl, LocalDateTime.now());
    assertNull(resultado);
  }

  @Test
  public void testHechosPendientes() {
    assertEquals(0, conexion.hechosPendientes());

    Hecho hecho = new Hecho.Builder()
        .titulo("Titulo")
        .descripcion("Desc")
        .categoria("Cat")
        .lugar("0","0", "Buenos Aires")
        .fechaAcontecimiento(LocalDateTime.now())
        .build();

    conexion.agregarHecho(hecho);

    assertEquals(1, conexion.hechosPendientes());
  }

}
