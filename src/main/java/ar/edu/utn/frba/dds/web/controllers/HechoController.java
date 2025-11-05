package ar.edu.utn.frba.dds.web.controllers;

import ar.edu.utn.frba.dds.coleccion.Coleccion;
import ar.edu.utn.frba.dds.fuente.dinamica.FuenteDinamica;
import ar.edu.utn.frba.dds.web.colecciones.ColeccionService;
import ar.edu.utn.frba.dds.web.hechos.HechoService;
import ar.edu.utn.frba.dds.web.security.AuthMiddleware;
import ar.edu.utn.frba.dds.web.views.ViewModelBuilder;
import io.javalin.http.Handler;
import java.util.List;
import java.util.Map;

public class HechoController {

  private final HechoService hechoService;
  private final ColeccionService coleccionService;

  public HechoController(HechoService hechoService, ColeccionService coleccionService) {
    this.hechoService = hechoService;
    this.coleccionService = coleccionService;
  }

  public Handler formularioNuevo() {
    return ctx -> {
      AuthMiddleware.requireUsuario(ctx);
      Map<String, Object> model = ViewModelBuilder.base(ctx);
      List<Coleccion> colecciones = coleccionService.listarColecciones().stream()
          .filter(c -> c.getFuente() instanceof FuenteDinamica)
          .toList();
      model.put("colecciones", colecciones);
      ctx.render("crear_hecho.hbs", model);
    };
  }

  public Handler crear() {
    return ctx -> {
      var usuario = AuthMiddleware.requireUsuario(ctx);
      Long coleccionId = Long.valueOf(ctx.formParam("coleccionId"));
      String titulo = ctx.formParam("titulo");
      String descripcion = ctx.formParam("descripcion");
      String categoria = ctx.formParam("categoria");
      String latitud = ctx.formParam("latitud");
      String longitud = ctx.formParam("longitud");
      String provincia = ctx.formParam("provincia");
      String fecha = ctx.formParam("fecha");

      try {
        hechoService.registrarHechoDinamico(coleccionId, titulo, descripcion, categoria,
            latitud, longitud, provincia, fecha, usuario.getEmail());
        ctx.sessionAttribute("flash", "Hecho enviado para revisión");
        ctx.redirect("/colecciones/" + coleccionId);
      } catch (Exception e) {
        ctx.sessionAttribute("error", e.getMessage());
        ctx.redirect("/hechos/nuevo");
      }
    };
  }
}
