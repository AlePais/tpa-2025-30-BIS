package ar.edu.utn.frba.dds;

import ar.edu.utn.frba.dds.fuente.proxy.FuenteDemo;
import ar.edu.utn.frba.dds.fuente.proxy.Conexion;
import ar.edu.utn.frba.dds.hecho.Hecho;
import java.time.LocalDateTime;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.net.URL;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FuenteDemoTest {
  private static final long MasDeUnaHora = 6_000;
  private URL url;
  private Conexion conexion;
  private FuenteDemo demo;
  Hecho hecho3;

  @BeforeEach
  public void init() throws Exception {
    conexion = new Conexion();
    url = new URL("http://test.com");

    // Preparamos hechos a consumir
    Hecho hecho1 = new Hecho.Builder()
        .titulo("H1")
        .descripcion("D1")
        .categoria("C1")
        .lugar("1","2","Buenos Aires")
        .fechaAcontecimiento(LocalDateTime.now())
        .build();

    Hecho hecho2 = new Hecho.Builder()
        .titulo("H2")
        .descripcion("D2")
        .categoria("C2")
        .lugar("3","4","Buenos Aires")
        .fechaAcontecimiento(LocalDateTime.now())
        .build();

    hecho3 = new Hecho.Builder()
        .titulo("H3")
        .descripcion("D3")
        .categoria("C3")
        .lugar("5","6","Buenos Aires")
        .fechaAcontecimiento(LocalDateTime.now())
        .build();

    conexion.agregarHecho(hecho1);
    conexion.agregarHecho(hecho2);

    demo = new FuenteDemo(conexion, url);
  }
  @Test
  public void losHechosDelaFuenteDemoSeActualizanSiEsperamosMasDeUnaHora() {
    List<Hecho> hechosAntes = demo.obtenerHechos();

    assertEquals(0, hechosAntes.size());

    //El cron actualiza los hechos del demo cada hora porlo tanto si ...
    esperamosMasDeunaHora(demo);
    //Se actualizaran los hechos de la fuente demo

    List<Hecho> hechosDespues = demo.obtenerHechos();

    assertEquals(2, hechosDespues.size());
    assertEquals("H1", hechosDespues.get(0).getTitulo());
    assertEquals("H2", hechosDespues.get(1).getTitulo());
  }

  @Test
  public void convierteUnHechoMapEnUnHecho() {
    Map<String, Object> hechoMapeado = Map.of(
        "titulo", "Noticia",
        "descripcion", "Desc",
        "categoria", "Cat",
        "latitud", "10.0",
        "longitud", "20.0",
        "fecha", LocalDate.of(2022, 6, 6)
    );

    Hecho hecho = demo.convertirAHecho(hechoMapeado);

    assertEquals("Noticia", hecho.getTitulo());
    assertEquals("10.0", hecho.getLatitud());
    assertEquals(LocalDate.of(2022, 6, 6), hecho.getFechaAcontecimiento());
  }

  private void esperamosMasDeunaHora(FuenteDemo demo) {
    demo.consultarHechosNuevos();
    demo.actualizarFechaConsultada();
    /**
      Para que esto de abajo funcione deberiamos tener el cron Exterior corriendo al mismo tiempo que el test
      try {
        Thread.sleep(MasDeUnaHora);
      } catch (InterruptedException e) {
        System.out.println("Esperamos a que pase una hora y ocurrio un error." + e.getMessage());
      }
    */

  }

  /**
    Esto seria con un cron interno lo dejo comentado, porque esta bueno
    Para que funcione se deberia DEScomentar los ScheduledExecutorService de la clase fuente demo.
  */
  /*  // -------- Antes ---------
  @Test
  public void losHechosDelaFuenteDemoSeActualizanSiEsperamosMasDeUnaHora() {
    //El cron actualiza los hechos del demo cada hora porlo tanto si ...
    esperamosMasDeunaHora();
    //Se actualizaran los hechos de la fuente demo

    List<Hecho> hechosDespues = demo.obtenerHechos();

    assertEquals(2, hechosDespues.size());
    assertEquals("H1", hechosDespues.get(0).getTitulo());
    assertEquals("H2", hechosDespues.get(1).getTitulo());

    demo.cerrarCronInterno();
  }

  @Test
  public void noActualizaLosHechosSiNoPasoUnaHoraMasDesdeLaUltimaActualizacion(){
    //El cron actualiza los hechos del demo cada hora porlo tanto si ...
    esperamosMasDeunaHora();
    //Se actualizaran los hechos de la fuente demo

    //Pero si la conexion agrega mas hechos y no pasó una hora desde que se activo el cron
    //No se veran reflejados en los hechos de la fuente demo
    conexion.agregarHecho(hecho3);

    List<Hecho> hechos = demo.obtenerHechos();

    assertEquals(2, hechos.size(), "No debería haber sincronizado el tercer hecho aún");

    demo.cerrarCronInterno();
  }

  @Test
  public void losHechosDelaFuenteDemoSeActualizanCadaHora(){
    //El cron actualiza los hechos del demo cada hora porlo tanto si ...
    esperamosMasDeunaHora();
    //Se actualizaran los hechos de la fuente demo

    // La conexion puede agregar mas hechos
    conexion.agregarHecho(hecho3);

    //el cron actualiza cada hora, porlo que si esperamos denuevo vuelve a actualizar los hechos del demo
    esperamosMasDeunaHora();

    List<Hecho> hechos = demo.obtenerHechos();
    assertEquals(3, hechos.size(), "Debería haber sincronizado el tercer hecho");

    demo.cerrarCronInterno();
  }

  @Test
  public void convierteUnHechoMapEnUnHecho() {
    Map<String, Object> hechoMapeado = Map.of(
        "titulo", "Noticia",
        "descripcion", "Desc",
        "categoria", "Cat",
        "latitud", "10.0",
        "longitud", "20.0",
        "fecha", LocalDate.of(2022, 6, 6)
    );

    Hecho hecho = demo.convertirAHecho(hechoMapeado);

    assertEquals("Noticia", hecho.getTitulo());
    assertEquals("10.0", hecho.getLatitud());
    assertEquals(LocalDate.of(2022, 6, 6), hecho.getFechaAcontecimiento());
  }

  private void esperamosMasDeunaHora() {
    try {
      Thread.sleep(MasDeUnaHora);
    } catch (InterruptedException e) {
      System.out.println("El hilo fue interrumpido mientras dormía." + e.getMessage());
    }
  }
  */
}
