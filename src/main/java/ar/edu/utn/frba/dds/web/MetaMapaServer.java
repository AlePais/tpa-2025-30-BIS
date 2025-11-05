package ar.edu.utn.frba.dds.web;

import ar.edu.utn.frba.dds.gestor_solicitudes_eliminacion.GestorSolicitudesEliminacion;
import ar.edu.utn.frba.dds.web.auth.AuthService;
import ar.edu.utn.frba.dds.web.auth.RepositorioUsuarios;
import ar.edu.utn.frba.dds.web.auth.UsuarioWeb;
import ar.edu.utn.frba.dds.web.controllers.AdminController;
import ar.edu.utn.frba.dds.web.controllers.AuthController;
import ar.edu.utn.frba.dds.web.controllers.ColeccionController;
import ar.edu.utn.frba.dds.web.controllers.HechoController;
import ar.edu.utn.frba.dds.web.controllers.SolicitudController;
import ar.edu.utn.frba.dds.web.colecciones.ColeccionService;
import ar.edu.utn.frba.dds.web.colecciones.FuenteFactory;
import ar.edu.utn.frba.dds.web.estadisticas.EstadisticasViewService;
import ar.edu.utn.frba.dds.web.hechos.HechoService;
import ar.edu.utn.frba.dds.web.security.AuthMiddleware;
import ar.edu.utn.frba.dds.web.solicitudes.SolicitudService;
import ar.edu.utn.frba.dds.web.views.HandlebarsRenderer;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.rendering.JavalinRenderer;
import java.util.Optional;

public class MetaMapaServer {

  private final ColeccionService coleccionService = new ColeccionService();
  private final AuthService authService = new AuthService(new RepositorioUsuarios());
  private final FuenteFactory fuenteFactory = new FuenteFactory();
  private final HechoService hechoService = new HechoService(coleccionService);
  private final SolicitudService solicitudService =
      new SolicitudService(GestorSolicitudesEliminacion.getInstancia());
  private final EstadisticasViewService estadisticasService = new EstadisticasViewService();

  private final ColeccionController coleccionController = new ColeccionController(coleccionService);
  private final AuthController authController = new AuthController(authService);
  private final HechoController hechoController = new HechoController(hechoService, coleccionService);
  private final SolicitudController solicitudController = new SolicitudController(solicitudService);
  private final AdminController adminController = new AdminController(
      coleccionService,
      solicitudService,
      estadisticasService,
      fuenteFactory
  );

  public static void main(String[] args) {
    new MetaMapaServer().start(7070);
  }

  public void start(int port) {
    inicializarAdminPorDefecto();
    JavalinRenderer.register(HandlebarsRenderer.INSTANCE, HandlebarsRenderer.EXTENSION);

    Javalin app = Javalin.create(config ->
        config.staticFiles.add(staticFiles -> {
          staticFiles.hostedPath = "/";
          staticFiles.location = Location.CLASSPATH;
          staticFiles.directory = "public";
        })
    );

    app.before(ctx -> {
      Long userId = ctx.sessionAttribute("userId");
      if (userId != null) {
        authService.buscarPorId(userId).ifPresent(usuario -> ctx.attribute("currentUser", usuario));
      }
    });

    app.before(ctx -> {
      String path = ctx.path();
      if (path.startsWith("/admin")) {
        try {
          AuthMiddleware.requireAdmin(ctx);
        } catch (io.javalin.http.UnauthorizedResponse e) {
          ctx.sessionAttribute("error", "Debés iniciar sesión como administrador/a");
          ctx.redirect("/login");
          return;
        } catch (io.javalin.http.ForbiddenResponse e) {
          ctx.status(403).result("Acceso denegado");
          return;
        }
      }
    });

    registrarRutasPublicas(app);
    registrarRutasProtegidas(app);

    app.start(port);
  }

  private void registrarRutasPublicas(Javalin app) {
    app.get("/", coleccionController.listar());
    app.get("/colecciones/{id}", coleccionController.detalle());

    app.get("/login", authController.mostrarLogin());
    app.post("/login", authController.login());
    app.get("/registro", authController.mostrarRegistro());
    app.post("/registro", authController.registrar());
    app.post("/logout", authController.logout());

    app.get("/solicitudes/nueva", solicitudController.formulario());
    app.post("/solicitudes", solicitudController.crear());
  }

  private void registrarRutasProtegidas(Javalin app) {
    app.get("/hechos/nuevo", hechoController.formularioNuevo());
    app.post("/hechos", hechoController.crear());

    app.get("/admin", adminController.panel());
    app.get("/admin/colecciones/nueva", adminController.nuevaColeccionForm());
    app.post("/admin/colecciones", adminController.crearColeccion());
    app.get("/admin/solicitudes", adminController.verSolicitudes());
    app.post("/admin/solicitudes/{id}/aprobar", adminController.aprobarSolicitud());
    app.post("/admin/solicitudes/{id}/rechazar", adminController.rechazarSolicitud());
    app.get("/admin/estadisticas", adminController.verEstadisticas());
    app.get("/admin/estadisticas/{tipo}", adminController.verEstadisticasPorTipo());
  }

  private void inicializarAdminPorDefecto() {
    Optional<UsuarioWeb> admin = authService.buscarPorEmail("admin@metamapa.org");
    if (admin.isEmpty()) {
      authService.registrar("Administración MetaMapa", "admin@metamapa.org",
          "metamapa", true);
    }
  }
}
