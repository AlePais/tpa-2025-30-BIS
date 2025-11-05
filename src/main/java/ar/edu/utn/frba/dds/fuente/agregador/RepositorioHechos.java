package ar.edu.utn.frba.dds.fuente.agregador;

import ar.edu.utn.frba.dds.consensuador.CriterioConsenso;
import ar.edu.utn.frba.dds.hecho.Hecho;
import ar.edu.utn.frba.dds.hecho.Lugar;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class RepositorioHechos {

  private final Map<String, List<Hecho>> hechosPorTitulo;
  private final Map<String, Set<CriterioConsenso>> consensosPorTitulo;
  private List<Hecho> hechosUnicos;

  public RepositorioHechos() {
    this.hechosPorTitulo = new LinkedHashMap<>();
    this.consensosPorTitulo = new HashMap<>();
    this.hechosUnicos = new ArrayList<>();
  }

  /**
    Obtiene todos los hechos preparados para la navegación.
  */
  public List<Hecho> obtenerHechos() {
    return List.copyOf(hechosUnicos);
  }

  /**
    Expone todos los hechos agregados, incluyendo duplicados, para el cálculo de consensos.
  */
  public List<Hecho> obtenerHechosParaConsenso() {
    return hechosPorTitulo.values().stream()
        .flatMap(List::stream)
        .toList();
  }

  /**
    Actualiza el repositorio con la sincronización más reciente de fuentes.
    Preserva las marcas de consenso para cada título cuando corresponda.
  */
  public void actualizarHechos(List<Hecho> hechosNuevos) {
    hechosPorTitulo.clear();
    Map<String, Hecho> unicos = new LinkedHashMap<>();
    Set<String> titulosPresentes = new LinkedHashSet<>();

    for (Hecho nuevo : hechosNuevos) {
      if (nuevo == null) {
        continue;
      }
      String claveTitulo = normalizarTitulo(nuevo.getTitulo());
      if (claveTitulo == null) {
        continue;
      }

      hechosPorTitulo.computeIfAbsent(claveTitulo, key -> new ArrayList<>()).add(nuevo);
      titulosPresentes.add(claveTitulo);

      Set<CriterioConsenso> previo = consensosPorTitulo.get(claveTitulo);
      if (previo == null || previo.isEmpty()) {
        nuevo.limpiarCriterioConsenso();
      } else {
        nuevo.setCriterioConsenso(new HashSet<>(previo));
      }

      String claveUnica = construirClaveUnica(nuevo);
      unicos.putIfAbsent(claveUnica, nuevo);
    }

    consensosPorTitulo.keySet().retainAll(titulosPresentes);
    this.hechosUnicos = new ArrayList<>(unicos.values());
    aplicarConsensos();
  }

  /**
    Registra los resultados de consenso calculados de forma asíncrona y los refleja en los hechos navegables.
  */
  public void registrarConsensos(Map<String, ? extends Set<CriterioConsenso>> consensosCalculados) {
    consensosPorTitulo.clear();
    if (consensosCalculados != null) {
      consensosCalculados.forEach((clave, valores) ->
          consensosPorTitulo.put(clave, copiarSet(valores)));
    }
    aplicarConsensos();
  }

  private void aplicarConsensos() {
    for (Hecho hecho : hechosUnicos) {
      String clave = normalizarTitulo(hecho.getTitulo());
      if (clave == null) {
        hecho.limpiarCriterioConsenso();
        continue;
      }
      Set<CriterioConsenso> consensos = consensosPorTitulo.get(clave);
      if (consensos == null || consensos.isEmpty()) {
        hecho.limpiarCriterioConsenso();
      } else {
        hecho.setCriterioConsenso(new HashSet<>(consensos));
      }
    }
  }

  private String construirClaveUnica(Hecho hecho) {
    String titulo = normalizarTitulo(hecho.getTitulo());
    String descripcion = normalizarTexto(hecho.getDescripcion());
    String categoria = normalizarTexto(hecho.getCategoria());
    Lugar lugar = hecho.getLugar();
    String latitud = lugar != null ? normalizarTexto(lugar.getLatitud()) : "";
    String longitud = lugar != null ? normalizarTexto(lugar.getLongitud()) : "";
    LocalDateTime fecha = hecho.getFechaAcontecimiento();
    return String.join("|", titulo, descripcion, categoria, latitud, longitud,
        fecha != null ? fecha.toString() : "");
  }

  private String normalizarTexto(String valor) {
    return valor == null ? "" : valor.trim().toLowerCase(Locale.ROOT);
  }

  private String normalizarTitulo(String titulo) {
    return titulo == null ? null : titulo.trim().toLowerCase(Locale.ROOT);
  }

  private Set<CriterioConsenso> copiarSet(Set<CriterioConsenso> valores) {
    if (valores == null || valores.isEmpty()) {
      return EnumSet.noneOf(CriterioConsenso.class);
    }
    return EnumSet.copyOf(valores);
  }
}
