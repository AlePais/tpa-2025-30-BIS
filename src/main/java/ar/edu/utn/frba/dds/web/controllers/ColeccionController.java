package ar.edu.utn.frba.dds.web.controllers;

import ar.edu.utn.frba.dds.coleccion.Coleccion;
import ar.edu.utn.frba.dds.consensuador.CriterioConsenso;
import ar.edu.utn.frba.dds.filtros.AtributoFiltro;
import ar.edu.utn.frba.dds.filtros.ParametroFiltro;
import ar.edu.utn.frba.dds.hecho.Hecho;
import ar.edu.utn.frba.dds.hecho.Lugar;
import ar.edu.utn.frba.dds.web.colecciones.ColeccionService;
import ar.edu.utn.frba.dds.web.views.ViewModelBuilder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.javalin.http.Handler;
import io.javalin.http.NotFoundResponse;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ColeccionController {

  private final ColeccionService coleccionService;
  private final ObjectMapper mapper = new ObjectMapper();

  public ColeccionController(ColeccionService coleccionService) {
    this.coleccionService = coleccionService;
  }

  public Handler listar() {
    return ctx -> {
      Map<String, Object> model = ViewModelBuilder.base(ctx);
      List<Coleccion> colecciones = coleccionService.listarColecciones();
      model.put("colecciones", colecciones.stream().map(this::mapearColeccion).toList());
      model.put("totalColecciones", colecciones.size());
      ctx.render("home.hbs", model);
    };
  }

  public Handler detalle() {
    return ctx -> {
      Long id = Long.valueOf(ctx.pathParam("id"));
      Coleccion coleccion = coleccionService.buscarPorId(id)
          .orElseThrow(NotFoundResponse::new);

      aplicarModoNavegacion(ctx.queryParam("modo"), coleccion);
      construirFiltros(coleccion, ctx.queryParamMap());

      List<Hecho> hechos = coleccion.obtenerHechos();
      Map<String, Object> model = ViewModelBuilder.base(ctx);
      model.put("coleccion", coleccion);
      model.put("hechos", hechos);
      model.put("hayHechos", !hechos.isEmpty());
      model.put("titulo", ctx.queryParam("titulo"));
      model.put("descripcion", ctx.queryParam("descripcion"));
      model.put("categoria", ctx.queryParam("categoria"));
      model.put("textoLibre", ctx.queryParam("textoLibre"));
      model.put("fecha", ctx.queryParam("fecha"));
      model.put("opcionesConsenso", construirOpcionesConsenso(coleccion));
      model.put("atributosFiltro", AtributoFiltro.values());
      List<Map<String, Object>> marcadores = construirMarcadores(hechos);
      model.put("marcadores", marcadores);
      model.put("marcadoresJson", serializarMarcadores(marcadores));

      String tituloSeleccionado = ctx.queryParam("tituloSeleccionado");
      if (tituloSeleccionado != null) {
        hechos.stream()
            .filter(h -> tituloSeleccionado.equalsIgnoreCase(h.getTitulo()))
            .findFirst()
            .ifPresent(hecho -> model.put("hechoSeleccionado", hecho));
      }

      ctx.render("coleccion_detalle.hbs", model);
    };
  }

  private Map<String, Object> mapearColeccion(Coleccion coleccion) {
    Map<String, Object> data = new HashMap<>();
    data.put("id", coleccion.getId());
    data.put("titulo", coleccion.getTitulo());
    data.put("descripcion", coleccion.getDescripcion());
    data.put("criterioConsenso", coleccion.getCriterioConsenso());
    data.put("navegacionCurada", Boolean.TRUE.equals(coleccion.getNavegacionCurada()));
    try {
      data.put("cantidadHechos", coleccion.obtenerHechos().size());
    } catch (Exception e) {
      data.put("cantidadHechos", 0);
    }
    return data;
  }

  private List<Map<String, Object>> construirMarcadores(List<Hecho> hechos) {
    return hechos.stream()
        .map(Hecho::getLugar)
        .filter(lugar -> lugar != null
            && lugar.getLatitud() != null && !lugar.getLatitud().isBlank()
            && lugar.getLongitud() != null && !lugar.getLongitud().isBlank())
        .map(this::mapearLugar)
        .collect(Collectors.toList());
  }

  private String serializarMarcadores(List<Map<String, Object>> marcadores) {
    try {
      return mapper.writeValueAsString(marcadores);
    } catch (JsonProcessingException e) {
      return "[]";
    }
  }

  private Map<String, Object> mapearLugar(Lugar lugar) {
    Map<String, Object> marker = new HashMap<>();
    marker.put("latitud", lugar.getLatitud());
    marker.put("longitud", lugar.getLongitud());
    marker.put("provincia", lugar.getProvincia());
    return marker;
  }

  private void aplicarModoNavegacion(String modo, Coleccion coleccion) {
    if (modo == null) {
      return;
    }
    boolean curada = "curado".equalsIgnoreCase(modo);
    coleccion.setNavegacionCurada(curada);
  }

  private void construirFiltros(Coleccion coleccion, Map<String, List<String>> params) {
    coleccion.getCriteriosFiltrado().clear();
    List<ParametroFiltro> filtros = new ArrayList<>();

    agregarFiltroSiCorresponde(params, "titulo", AtributoFiltro.TITULO, filtros);
    agregarFiltroSiCorresponde(params, "descripcion", AtributoFiltro.DESCRIPCION, filtros);
    agregarFiltroSiCorresponde(params, "categoria", AtributoFiltro.CATEGORIA, filtros);
    agregarFiltroSiCorresponde(params, "textoLibre", AtributoFiltro.TEXTO_LIBRE, filtros);
    agregarFiltroSiCorresponde(params, "latitud", AtributoFiltro.LATITUD, filtros);
    agregarFiltroSiCorresponde(params, "longitud", AtributoFiltro.LONGITUD, filtros);
    agregarFiltroSiCorresponde(params, "fecha", AtributoFiltro.FECHA_HECHO, filtros);

    String consenso = obtenerPrimero(params, "consenso");
    if (consenso != null && !consenso.isBlank()) {
      coleccion.setCriterioConsenso(CriterioConsenso.valueOf(consenso));
    }

    filtros.forEach(coleccion::agregarCriterioFiltrado);
  }

  private List<Map<String, Object>> construirOpcionesConsenso(Coleccion coleccion) {
    String seleccionado = coleccion.getCriterioConsenso() == null
        ? ""
        : coleccion.getCriterioConsenso().name();
    return java.util.Arrays.stream(CriterioConsenso.values())
        .map(valor -> {
          Map<String, Object> opcion = new HashMap<>();
          opcion.put("valor", valor.name());
          opcion.put("texto", valor.name());
          opcion.put("seleccionado", valor.name().equals(seleccionado));
          return opcion;
        })
        .collect(Collectors.toList());
  }

  private void agregarFiltroSiCorresponde(Map<String, List<String>> params, String nombre,
                                          AtributoFiltro atributo, List<ParametroFiltro> filtros) {
    String valor = obtenerPrimero(params, nombre);
    if (valor != null && !valor.isBlank()) {
      filtros.add(new ParametroFiltro(atributo, valor));
    }
  }

  private String obtenerPrimero(Map<String, List<String>> params, String nombre) {
    List<String> valores = params.get(nombre);
    if (valores == null || valores.isEmpty()) {
      return null;
    }
    return valores.get(0);
  }
}
