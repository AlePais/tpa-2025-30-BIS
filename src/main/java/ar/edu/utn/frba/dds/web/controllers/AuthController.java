package ar.edu.utn.frba.dds.web.controllers;

import ar.edu.utn.frba.dds.web.auth.AuthService;
import ar.edu.utn.frba.dds.web.auth.UsuarioWeb;
import ar.edu.utn.frba.dds.web.views.ViewModelBuilder;
import io.javalin.http.Context;
import io.javalin.http.Handler;
import java.util.Map;

public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  public Handler mostrarLogin() {
    return ctx -> render(ctx, "login.hbs", Map.of());
  }

  public Handler mostrarRegistro() {
    return ctx -> render(ctx, "registro.hbs", Map.of());
  }

  public Handler registrar() {
    return ctx -> {
      String nombre = ctx.formParam("nombre");
      String email = ctx.formParam("email");
      String password = ctx.formParam("password");
      boolean admin = "on".equalsIgnoreCase(ctx.formParam("admin"));
      try {
        UsuarioWeb usuario = authService.registrar(nombre, email, password, admin);
        ctx.sessionAttribute("userId", usuario.getId());
        ctx.sessionAttribute("flash", "¡Registro exitoso!");
        ctx.redirect("/");
      } catch (IllegalArgumentException e) {
        ctx.sessionAttribute("error", e.getMessage());
        ctx.redirect("/registro");
      }
    };
  }

  public Handler login() {
    return ctx -> {
      String email = ctx.formParam("email");
      String password = ctx.formParam("password");
      authService.autenticar(email, password)
          .ifPresentOrElse(usuario -> {
                ctx.sessionAttribute("userId", usuario.getId());
                ctx.sessionAttribute("flash", "Sesión iniciada correctamente");
                ctx.redirect("/");
              },
              () -> {
                ctx.sessionAttribute("error", "Credenciales inválidas");
                ctx.redirect("/login");
              }
          );
    };
  }

  public Handler logout() {
    return ctx -> {
      ctx.req().getSession().invalidate();
      ctx.redirect("/");
    };
  }

  private void render(Context ctx, String template, Map<String, Object> extra) {
    Map<String, Object> model = ViewModelBuilder.base(ctx);
    model.putAll(extra);
    ctx.render(template, model);
  }
}
