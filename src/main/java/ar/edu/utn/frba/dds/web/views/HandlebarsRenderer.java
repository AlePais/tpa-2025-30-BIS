package ar.edu.utn.frba.dds.web.views;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;
import io.javalin.http.Context;
import io.javalin.rendering.FileRenderer;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class HandlebarsRenderer implements FileRenderer {

  public static final HandlebarsRenderer INSTANCE = new HandlebarsRenderer();
  public static final String EXTENSION = ".hbs";

  private final Handlebars handlebars;
  private final Map<String, Template> cache = new ConcurrentHashMap<>();

  private HandlebarsRenderer() {
    ClassPathTemplateLoader loader = new ClassPathTemplateLoader();
    loader.setPrefix("/templates");
    loader.setSuffix(EXTENSION);
    this.handlebars = new Handlebars(loader);
  }

  @Override
  public String render(String filePath, Map<String, ?> model, Context context) {
    try {
      Template template = cache.computeIfAbsent(normalize(filePath), this::compile);
      Map<String, ?> safeModel = model == null ? Collections.emptyMap() : model;
      return template.apply(safeModel);
    } catch (UncheckedIOException e) {
      throw new RuntimeException("No se pudo renderizar la plantilla Handlebars: " + filePath,
          e.getCause());
    } catch (IOException e) {
      throw new RuntimeException("Error al aplicar la plantilla Handlebars: " + filePath, e);
    }
  }

  private Template compile(String templateName) {
    try {
      return handlebars.compile(templateName);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  private String normalize(String filePath) {
    String name = filePath;
    if (name.startsWith("/")) {
      name = name.substring(1);
    }
    if (name.endsWith(EXTENSION)) {
      name = name.substring(0, name.length() - EXTENSION.length());
    }
    return name;
  }
}
