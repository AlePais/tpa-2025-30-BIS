package ar.edu.utn.frba.dds.web.controllers;

import ar.edu.utn.frba.dds.web.security.AuthMiddleware;
import ar.edu.utn.frba.dds.web.solicitudes.SolicitudService;
import ar.edu.utn.frba.dds.web.views.ViewModelBuilder;
import io.javalin.http.Handler;
import java.util.Map;

public class SolicitudController {

  private final SolicitudService solicitudService;

  public SolicitudController(SolicitudService solicitudService) {
    this.solicitudService = solicitudService;
  }

  public Handler formulario() {
    return ctx -> {
      AuthMiddleware.requireUsuario(ctx);
      Map<String, Object> model = ViewModelBuilder.base(ctx);
      model.put("titulo", ctx.queryParam("titulo"));
      ctx.render("solicitud_eliminacion.hbs", model);
    };
  }

  public Handler crear() {
    return ctx -> {
      AuthMiddleware.requireUsuario(ctx);
      String titulo = ctx.formParam("titulo");
      String motivo = ctx.formParam("motivo");
      try {
        solicitudService.crearSolicitud(titulo, motivo);
        ctx.sessionAttribute("flash", "Solicitud enviada para revisión");
        ctx.redirect("/");
      } catch (IllegalArgumentException e) {
        ctx.sessionAttribute("error", e.getMessage());
        ctx.redirect("/solicitudes/nueva?titulo=" + titulo);
      }
    };
  }
}
