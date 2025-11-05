package ar.edu.utn.frba.dds.fuente.proxy;

import ar.edu.utn.frba.dds.fuente.Fuente;
import ar.edu.utn.frba.dds.hecho.Hecho;
import ar.edu.utn.frba.dds.gestor_solicitudes_eliminacion.SolicitudEliminacion;
import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.*;
import java.util.*;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.net.URLEncoder;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@DiscriminatorValue("METAMAPA")
public class AdaptadorMetaMapa extends Fuente {

  @Transient
  private HttpClient clienteHttp = HttpClient.newHttpClient();
  @Transient
  private ObjectMapper mapper;
  @Transient
  private URL url;
  @Column(name = "url_base")
  private String urlString;
  private String coleccion;
  @ElementCollection
  @CollectionTable(name = "filtros_metamapa", joinColumns = @JoinColumn(name = "fuente_id"))
  @MapKeyColumn(name = "clave")
  @Column(name = "valor")
  private Map<String, String> filtro = new HashMap<>();

  public AdaptadorMetaMapa() {
    this.initTransientes();
  }

  public AdaptadorMetaMapa(URL baseUrl, String coleccion, Map<String, String> filtro) {
    this.url = baseUrl;
    this.urlString = baseUrl.toString();
    this.coleccion = coleccion;
    this.filtro = filtro;
    this.initTransientes();
  }

  @PostLoad
  public void postLoad() {
    try {
      this.url = new URL(this.urlString);
    } catch (Exception e) {
      throw new RuntimeException("URL inválida: " + urlString, e);
    }
    this.initTransientes();
  }

  private void initTransientes() {
    this.clienteHttp = HttpClient.newHttpClient();
    this.mapper = new ObjectMapper();
    this.mapper.registerModule(new JavaTimeModule());
    this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
  }

  @Override
  public List<Hecho> obtenerHechos() {
    if (coleccion != null && filtro != null) {
      return consultarHechosDeColeccionConFiltros(coleccion, filtro);
    } else if (coleccion != null) {
      return consultarHechosDeColeccion(coleccion);
    } else if (filtro != null) {
      return consultarHechosConFiltros(filtro);
    } else {
      return consultarHechos();
    }
  }

  public void solicitarEliminacion(SolicitudEliminacion solicitud) {
    try {
      String json = mapper.writeValueAsString(solicitud);

      HttpRequest request = HttpRequest.newBuilder()
          .uri(URI.create(url + "/solicitudes"))
          .header("Content-Type", "application/json")
          .POST(HttpRequest.BodyPublishers.ofString(json))
          .build();

      HttpResponse<String> response = clienteHttp.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() != 200 && response.statusCode() != 201) {
        throw new RuntimeException("Error al enviar solicitud de eliminación: código " + response.statusCode());
      }

    } catch (IOException | InterruptedException e) {
      throw new RuntimeException("Error al enviar solicitud de eliminación", e);
    }
  }

  private List<Hecho> consultarHechos() {
    try {
      URL url = new URL(this.url, "/hechos");
      return realizarGetHechos(url);
    } catch (Exception e) {
      throw new RuntimeException("Error al consultar todos los hechos", e);
    }
  }

  private List<Hecho> consultarHechosConFiltros(Map<String, String> filtros) {
    try {
      String query = construirQueryParams(filtros);
      URL url = new URL(this.url, "/hechos" + query);
      return realizarGetHechos(url);
    } catch (Exception e) {
      throw new RuntimeException("Error al obtener hechos con filtros", e);
    }
  }

  private List<Hecho> consultarHechosDeColeccion(String idColeccion) {
    try {
      URL url = new URL(this.url, "/colecciones/" + idColeccion + "/hechos");
      return realizarGetHechos(url);
    } catch (Exception e) {
      throw new RuntimeException("Error al obtener hechos de la colección " + idColeccion, e);
    }
  }

  private List<Hecho> consultarHechosDeColeccionConFiltros(String idColeccion, Map<String, String> filtros) {
    try {
      String query = construirQueryParams(filtros);
      URL url = new URL(this.url, "/colecciones/" + idColeccion + "/hechos" + query);
      return realizarGetHechos(url);
    } catch (Exception e) {
      throw new RuntimeException("Error al obtener hechos con filtros de la colección " + idColeccion, e);
    }
  }

  private List<Hecho> realizarGetHechos(URL url) {
    try {
      HttpRequest request = HttpRequest.newBuilder()
          .uri(url.toURI())
          .GET()
          .build();

      HttpResponse<String> response = clienteHttp.send(request, HttpResponse.BodyHandlers.ofString());

      if (response.statusCode() == 200) {
        return mapper.readValue(response.body(), new TypeReference<>() {});
      } else {
        throw new RuntimeException("Error en la respuesta HTTP: código " + response.statusCode());
      }
    } catch (Exception e) {
      throw new RuntimeException("Error al realizar GET de hechos desde URL: " + url, e);
    }
  }

  private String construirQueryParams(Map<String, String> filtros) {
    if (filtros == null || filtros.isEmpty()) return "";

    StringBuilder query = new StringBuilder("?");
    filtros.forEach((k, v) -> {
      try {
        query.append(URLEncoder.encode(k, "UTF-8"))
            .append("=")
            .append(URLEncoder.encode(v, "UTF-8"))
            .append("&");
      } catch (Exception e) {
        throw new RuntimeException("Error al codificar parámetro de filtro: " + k, e);
      }
    });

    query.setLength(query.length() - 1); // Elimina el último &
    return query.toString();
  }
}


