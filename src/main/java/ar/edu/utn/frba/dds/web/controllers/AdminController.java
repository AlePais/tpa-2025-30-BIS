package ar.edu.utn.frba.dds.web.controllers;

import ar.edu.utn.frba.dds.consensuador.CriterioConsenso;
import ar.edu.utn.frba.dds.estadistica.TipoEstadistica;
import ar.edu.utn.frba.dds.gestor_solicitudes_eliminacion.SolicitudEliminacion;
import ar.edu.utn.frba.dds.web.colecciones.ColeccionService;
import ar.edu.utn.frba.dds.web.colecciones.FuenteFactory;
import ar.edu.utn.frba.dds.web.estadisticas.EstadisticasViewService;
import ar.edu.utn.frba.dds.web.solicitudes.SolicitudService;
import ar.edu.utn.frba.dds.web.views.ViewModelBuilder;
import io.javalin.http.Handler;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminController {

  private final ColeccionService coleccionService;
  private final SolicitudService solicitudService;
  private final EstadisticasViewService estadisticasService;
  private final FuenteFactory fuenteFactory;

  public AdminController(ColeccionService coleccionService,
                         SolicitudService solicitudService,
                         EstadisticasViewService estadisticasService,
                         FuenteFactory fuenteFactory) {
    this.coleccionService = coleccionService;
    this.solicitudService = solicitudService;
    this.estadisticasService = estadisticasService;
    this.fuenteFactory = fuenteFactory;
  }

  public Handler panel() {
    return ctx -> {
      Map<String, Object> model = ViewModelBuilder.base(ctx);
      model.put("totalColecciones", coleccionService.listarColecciones().size());
      model.put("totalSolicitudes", solicitudService.pendientes().size());
      ctx.render("admin_panel.hbs", model);
    };
  }

  public Handler nuevaColeccionForm() {
    return ctx -> {
      Map<String, Object> model = ViewModelBuilder.base(ctx);
      model.put("criteriosConsenso", CriterioConsenso.values());
      ctx.render("admin_coleccion_form.hbs", model);
    };
  }

  public Handler crearColeccion() {
    return ctx -> {
      String titulo = ctx.formParam("titulo");
      String descripcion = ctx.formParam("descripcion");
      String tipoFuente = ctx.formParam("tipoFuente");
      CriterioConsenso consenso = CriterioConsenso.valueOf(ctx.formParam("criterioConsenso"));

      Map<String, String> parametros = new HashMap<>();
      parametros.put("path", ctx.formParam("path"));
      parametros.put("url", ctx.formParam("url"));

      try {
        var fuente = fuenteFactory.crearFuente(tipoFuente, parametros);
        coleccionService.crearColeccion(titulo, descripcion, fuente, consenso, List.of());
        ctx.sessionAttribute("flash", "Colección creada correctamente");
        ctx.redirect("/admin");
      } catch (IllegalArgumentException e) {
        ctx.sessionAttribute("error", e.getMessage());
        ctx.redirect("/admin/colecciones/nueva");
      }
    };
  }

  public Handler verSolicitudes() {
    return ctx -> {
      Map<String, Object> model = ViewModelBuilder.base(ctx);
      List<SolicitudEliminacion> pendientes = solicitudService.pendientes();
      model.put("solicitudes", pendientes);
      ctx.render("admin_solicitudes.hbs", model);
    };
  }

  public Handler aprobarSolicitud() {
    return ctx -> {
      Long id = Long.valueOf(ctx.pathParam("id"));
      solicitudService.aprobar(id);
      ctx.redirect("/admin/solicitudes");
    };
  }

  public Handler rechazarSolicitud() {
    return ctx -> {
      Long id = Long.valueOf(ctx.pathParam("id"));
      solicitudService.rechazar(id);
      ctx.redirect("/admin/solicitudes");
    };
  }

  public Handler verEstadisticas() {
    return ctx -> {
      Map<String, Object> model = ViewModelBuilder.base(ctx);
      model.put("ultimaCorrida", estadisticasService.obtenerUltimas());
      model.put("estadisticasPorTipo", TipoEstadistica.values());
      ctx.render("estadisticas.hbs", model);
    };
  }

  public Handler verEstadisticasPorTipo() {
    return ctx -> {
      Map<String, Object> model = ViewModelBuilder.base(ctx);
      TipoEstadistica tipo = TipoEstadistica.valueOf(ctx.pathParam("tipo"));
      model.put("tipoSeleccionado", tipo);
      model.put("estadisticas", estadisticasService.obtenerPorTipo(tipo));
      ctx.render("estadisticas_tipo.hbs", model);
    };
  }
}
