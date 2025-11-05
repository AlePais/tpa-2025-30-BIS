package ar.edu.utn.frba.dds.web.security;

import ar.edu.utn.frba.dds.web.auth.UsuarioWeb;
import io.javalin.http.Context;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.UnauthorizedResponse;

public final class AuthMiddleware {

  private AuthMiddleware() {
  }

  public static UsuarioWeb requireUsuario(Context ctx) {
    UsuarioWeb usuario = ctx.attribute("currentUser");
    if (usuario == null) {
      throw new UnauthorizedResponse("Debe iniciar sesión");
    }
    return usuario;
  }

  public static UsuarioWeb requireAdmin(Context ctx) {
    UsuarioWeb usuario = requireUsuario(ctx);
    if (!usuario.esAdmin()) {
      throw new ForbiddenResponse("Acceso restringido a personas administradoras");
    }
    return usuario;
  }
}
