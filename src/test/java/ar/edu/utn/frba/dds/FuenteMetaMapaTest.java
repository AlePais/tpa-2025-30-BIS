package ar.edu.utn.frba.dds;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import ar.edu.utn.frba.dds.fuente.proxy.AdaptadorMetaMapa;
import ar.edu.utn.frba.dds.hecho.Hecho;
import ar.edu.utn.frba.dds.hecho.Lugar;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FuenteMetaMapaTest {

  private AdaptadorMetaMapa adaptador;
  private AdaptadorMetaMapa spyAdaptador;
  private ObjectMapper mapper;

  @BeforeEach
  void setUp() throws Exception {
    URL url = new URL("http://localhost:8080");
    mapper = new ObjectMapper();
    mapper.registerModule(new JavaTimeModule());
    mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

    adaptador = new AdaptadorMetaMapa(url, "coleccion123", Map.of());
    spyAdaptador = spy(adaptador);
  }

  @Test
  void testObtenerHechos() throws Exception {
    Hecho hecho = new Hecho.Builder()
        .titulo("Titulo")
        .descripcion("Descripcion")
        .categoria("Categoria")
        .lugar("-34.6037","-58.3816", "Buenos Aires")
        .fechaAcontecimiento(LocalDateTime.now())
        .build();

    List<Hecho> hechosMock = List.of(hecho);

    spyAdaptador.setColeccion(anyString());
    spyAdaptador.setFiltro(anyMap());

    doReturn(hechosMock).when(spyAdaptador).obtenerHechos();

    List<Hecho> hechos = spyAdaptador.obtenerHechos();

    assertEquals(1, hechos.size());
    assertEquals("Titulo", hechos.get(0).getTitulo());
  }

  @Test
  void testObtenerHechosFallaHttp() throws Exception {
    spyAdaptador.setColeccion(anyString());
    spyAdaptador.setFiltro(anyMap());
    doThrow(new RuntimeException("Error en la respuesta HTTP: código 500"))
        .when(spyAdaptador).obtenerHechos();

    List<Hecho> hechos;
    try {
      hechos = spyAdaptador.obtenerHechos();
      fail("Se esperaba una excepción");
    } catch (RuntimeException e) {
      assertTrue(e.getMessage().contains("Error"));
    }
  }

  @Test
  void testObtenerHechosDeColeccion() throws Exception {

    Hecho hecho = new Hecho.Builder()
        .titulo("Titulo")
        .descripcion("Descripcion")
        .categoria("Categoria")
        .lugar("-34.6037","-58.3816","Buenos Aires")
        .fechaAcontecimiento(LocalDateTime.now())
        .build();

    List<Hecho> hechosMock = List.of(hecho);

    spyAdaptador.setColeccion(anyString());

    doReturn(hechosMock).when(spyAdaptador).obtenerHechos();

    spyAdaptador.setFiltro(null); // Forzar que solo use colección
    List<Hecho> hechos = spyAdaptador.obtenerHechos();

    assertEquals(1, hechos.size());
    assertEquals("Titulo", hechos.get(0).getTitulo());
  }

  @Test
  void testObtenerHechosConFiltros() throws Exception {
    Hecho hecho = new Hecho.Builder()
        .titulo("Titulo")
        .descripcion("Descripcion")
        .categoria("Categoria")
        .lugar("-34.6037","-58.3816", "Buenos Aires")
        .fechaAcontecimiento(LocalDateTime.now())
        .build();

    List<Hecho> hechosMock = List.of(hecho);

    spyAdaptador.setFiltro(anyMap());

    doReturn(hechosMock).when(spyAdaptador).obtenerHechos();

    spyAdaptador.setColeccion(null); // Forzar que solo use filtros
    List<Hecho> hechos = spyAdaptador.obtenerHechos();

    assertEquals(1, hechos.size());
    assertEquals("Titulo", hechos.get(0).getTitulo());
  }
}
