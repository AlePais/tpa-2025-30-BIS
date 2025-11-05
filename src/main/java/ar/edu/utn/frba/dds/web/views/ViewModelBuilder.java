package ar.edu.utn.frba.dds.web.views;

import ar.edu.utn.frba.dds.web.auth.UsuarioWeb;
import io.javalin.http.Context;
import java.util.HashMap;
import java.util.Map;

public final class ViewModelBuilder {

  private ViewModelBuilder() {
  }

  public static Map<String, Object> base(Context ctx) {
    Map<String, Object> model = new HashMap<>();
    UsuarioWeb usuario = ctx.attribute("currentUser");
    if (usuario != null) {
      model.put("usuario", usuario);
      model.put("esAdmin", usuario.esAdmin());
    }
    String flash = ctx.consumeSessionAttribute("flash");
    if (flash != null) {
      model.put("flash", flash);
    }
    String error = ctx.consumeSessionAttribute("error");
    if (error != null) {
      model.put("error", error);
    }
    return model;
  }
}
