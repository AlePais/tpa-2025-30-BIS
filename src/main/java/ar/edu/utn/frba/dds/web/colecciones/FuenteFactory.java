package ar.edu.utn.frba.dds.web.colecciones;

import ar.edu.utn.frba.dds.fuente.Fuente;
import ar.edu.utn.frba.dds.fuente.agregador.FuenteAgregador;
import ar.edu.utn.frba.dds.fuente.dinamica.FuenteDinamica;
import ar.edu.utn.frba.dds.fuente.estatica.FuenteEstatica;
import ar.edu.utn.frba.dds.fuente.proxy.Conexion;
import ar.edu.utn.frba.dds.fuente.proxy.FuenteDemo;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Map;

public class FuenteFactory {

  public Fuente crearFuente(String tipoFuente, Map<String, String> parametros) {
    if (tipoFuente == null) {
      return null;
    }
    String tipoNormalizado = tipoFuente.trim().toUpperCase(Locale.ROOT);
    switch (tipoNormalizado) {
      case "ESTATICA":
        return crearFuenteEstatica(parametros.get("path"));
      case "DINAMICA":
        return new FuenteDinamica();
      case "DEMO":
      case "PROXY":
        return crearFuenteDemo(parametros.get("url"));
      case "AGREGADOR":
        return new FuenteAgregador();
      default:
        throw new IllegalArgumentException("Tipo de fuente no soportado: " + tipoFuente);
    }
  }

  private Fuente crearFuenteEstatica(String path) {
    if (path == null || path.isBlank()) {
      throw new IllegalArgumentException("Debe indicar la ruta del dataset CSV");
    }
    return new FuenteEstatica(path);
  }

  private Fuente crearFuenteDemo(String url) {
    if (url == null || url.isBlank()) {
      throw new IllegalArgumentException("Debe indicar la URL del servicio demo");
    }
    try {
      return new FuenteDemo(new Conexion(), new URL(url));
    } catch (MalformedURLException e) {
      throw new IllegalArgumentException("La URL de la fuente demo es inválida", e);
    }
  }
}
