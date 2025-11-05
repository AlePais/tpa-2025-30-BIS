package ar.edu.utn.frba.dds;

import ar.edu.utn.frba.dds.fuente.dinamica.hechos_dinamicos.EstadoHechoDinamico;
import ar.edu.utn.frba.dds.fuente.dinamica.FuenteDinamica;
import ar.edu.utn.frba.dds.fuente.dinamica.RepositorioHechosySolicitudes;
import ar.edu.utn.frba.dds.fuente.dinamica.hechos_dinamicos.HechoDinamico;
import ar.edu.utn.frba.dds.hecho.Lugar;
import java.time.LocalDate;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class FuenteDinamicaTest {
  HechoDinamico hechoNuevoSinUsuario;
  HechoDinamico hechoNuevoConUsuario;
  HechoDinamico hechoMuyAntiguo;
  RepositorioHechosySolicitudes repoHechosySolicitudes;

  @BeforeEach
  public void init() {
    hechoNuevoSinUsuario = new HechoDinamico(
        "hechoNuevoSinUsuario",
        "descripcion",
        "categoria",
        new Lugar("latitud", "longitud","provincia"),
        LocalDateTime.now(),
        null );

    hechoNuevoConUsuario = new HechoDinamico(
        "hechoNuevoConUsuario",
        "descripcion",
        "categoria",
        new Lugar("latitud", "longitud","provincia"),
        LocalDateTime.now(),
        "juan");
    //System.out.println("es: " + hechoNuevoConUsuario.getUsuario());

    hechoMuyAntiguo = new HechoDinamico(
        "hechoMuyAntiguo",
        "descripcion",
        "categoria",
        new Lugar("latitud", "longitud","provincia"),
        LocalDateTime.parse("2025-04-29"),
        "Pepe");
    hechoMuyAntiguo.setEstado(EstadoHechoDinamico.APROBADO);

    repoHechosySolicitudes = RepositorioHechosySolicitudes.getInstancia();
    repoHechosySolicitudes.reset();
  }

  @Test void unHechoAntiguoNosePuedeEditar() {

    Assertions.assertFalse(hechoMuyAntiguo.aunEsEditable());
  }

  @Test void unHechoNuevoConUsuarioSiSePuedeEditar() {

    Assertions.assertTrue(hechoNuevoConUsuario.aunEsEditable());
  }

  @Test void unHechoSinUsuarioNoSePuedeEditar() {

    Assertions.assertFalse(hechoNuevoSinUsuario.aunEsEditable());
  }

  @Test void vuelveAestadoPendienteLaSugerenciaDeUnCambioEnUnHecho() {
    hechoNuevoConUsuario.aprobar();

    hechoNuevoConUsuario.modificarHecho("juan", "algunaSugerencia");

    Assertions.assertEquals(EstadoHechoDinamico.PENDIENTE, hechoNuevoConUsuario.getEstado());
  }

  @Test
  public void guardarHechosNuevosEnElRepositorio() {
    FuenteDinamica fuente = new FuenteDinamica(repoHechosySolicitudes);

    fuente.guardarSolicitudHecho(hechoNuevoConUsuario);
    fuente.guardarSolicitudHecho(hechoNuevoSinUsuario);

    Assertions.assertEquals(2, fuente.obtenerHechosPendientes().size());
    Assertions.assertEquals(0, fuente.obtenerHechos().size());
  }

  @Test
  public void guardarHechosAprobadosEnElRepositorio() {
    FuenteDinamica fuente = new FuenteDinamica(repoHechosySolicitudes);

    hechoNuevoSinUsuario.aprobar();
    hechoNuevoConUsuario.aprobar();

    fuente.guardarSolicitudHecho(hechoNuevoConUsuario);
    fuente.guardarSolicitudHecho(hechoNuevoSinUsuario);

    /*repoHechosySolicitudes.exportarHechosDinamicosAJson();*/

    Assertions.assertEquals(0, fuente.obtenerHechosPendientes().size());
    Assertions.assertEquals(2, fuente.obtenerHechos().size());
  }

  @Test
  public void mostrarLosHechosAprobados() {
    FuenteDinamica fuente = new FuenteDinamica(repoHechosySolicitudes);

    hechoNuevoSinUsuario.aprobar();
    hechoNuevoConUsuario.aprobar();

    fuente.guardarSolicitudHecho(hechoNuevoConUsuario);
    fuente.guardarSolicitudHecho(hechoNuevoSinUsuario);

    fuente.obtenerHechos().forEach(h->System.out.println("es: " + h.getTitulo()));
  }

}

