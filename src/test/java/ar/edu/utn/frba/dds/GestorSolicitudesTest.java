package ar.edu.utn.frba.dds;

import static org.junit.jupiter.api.Assertions.*;

import ar.edu.utn.frba.dds.gestor_solicitudes_eliminacion.GestorSolicitudesEliminacion;
import ar.edu.utn.frba.dds.gestor_solicitudes_eliminacion.SolicitudEliminacion;
import org.junit.jupiter.api.Test;

class GestorSolicitudesTest {

  @Test
  public void noPermiteCrearSolicitudesDeEliminacionDeMenosDe500Caracteres(){
    assertThrows(IllegalArgumentException.class, () -> {
      new SolicitudEliminacion("Incendio forestal", textoMenos500Caracteres());
    });
  }

  @Test
  public void CrearSolicitudesDeEliminacionDeMasDe500Caracteres(){
    var titulo = "inundacion";
    SolicitudEliminacion solicitudEliminacion = new SolicitudEliminacion(titulo, textoMasDe500Caracteres());
    assertEquals(textoMasDe500Caracteres(), solicitudEliminacion.getMotivo());
  }

  @Test
  public void agregar2SolicitudesALista(){
    var gestorSolicitudes = gestorSolicitudes();
    gestorSolicitudes.agregarSolicitud(solicitudEliminacionIncendioForestal());
    gestorSolicitudes.agregarSolicitud(solicitudEliminacionInundacion());
    assertEquals(2, gestorSolicitudes.cantidadSolicitudesPendientes());
  }

  @Test
  public void aprobarSolicitud(){
    var gestorSolicitudes = gestorSolicitudes();
    gestorSolicitudes.agregarSolicitud(solicitudEliminacionIncendioForestal());
    gestorSolicitudes.agregarSolicitud(solicitudEliminacionInundacion());
    gestorSolicitudes.aprobarSolicitud(solicitudEliminacionIncendioForestal());
  }

  @Test
  public void intentoAprobarSolicitudInexistente(){
    var gestorSolicitudes = gestorSolicitudes();
    gestorSolicitudes.agregarSolicitud(solicitudEliminacionIncendioForestal());
    assertThrows(IllegalArgumentException.class, () -> {
      gestorSolicitudes.aprobarSolicitud(solicitudEliminacionInundacion());
    });
  }

  @Test
  public void desaprobarSolicitudEliminacion(){
    var gestorSolicitudes = gestorSolicitudes();
    gestorSolicitudes.agregarSolicitud(solicitudEliminacionIncendioForestal());
    gestorSolicitudes.agregarSolicitud(solicitudEliminacionInundacion());
    gestorSolicitudes.desaprobarSolicitud(solicitudEliminacionIncendioForestal());
  }

  @Test
  public void verificarEliminado(){
    var tituloHechoAVerificar = "Incendio forestal";
    var gestorSolicitudes = gestorSolicitudes();
    gestorSolicitudes.agregarSolicitud(solicitudEliminacionIncendioForestal());
    gestorSolicitudes.agregarSolicitud(solicitudEliminacionInundacion());
    gestorSolicitudes.aprobarSolicitud(solicitudEliminacionIncendioForestal());
    assertTrue(gestorSolicitudes.siEstaEliminado(tituloHechoAVerificar));
  }

  private String textoMenos500Caracteres() {
    return "Texto de menos de 500 caracteres. ";
  }

  private String textoMasDe500Caracteres() {
    String texto = "Texto de mas de 500 caracteres. ";
    return texto.repeat(16);
  }

  private GestorSolicitudesEliminacion gestorSolicitudes() {
    GestorSolicitudesEliminacion gestor = GestorSolicitudesEliminacion.getInstancia();
    gestor.reset();
    return gestor;
  }

  private SolicitudEliminacion solicitudEliminacionIncendioForestal() {
    return new SolicitudEliminacion("Incendio forestal", textoMasDe500Caracteres());
  }

  private SolicitudEliminacion solicitudEliminacionInundacion() {
    return new SolicitudEliminacion("Inundacion", textoMasDe500Caracteres());
  }
}