package ar.edu.utn.frba.dds.web.colecciones;

import ar.edu.utn.frba.dds.coleccion.Coleccion;
import ar.edu.utn.frba.dds.coleccion.repositorio.RepositorioColecciones;
import ar.edu.utn.frba.dds.coleccion.repositorio.RepositorioColeccionesJPA;
import ar.edu.utn.frba.dds.consensuador.CriterioConsenso;
import ar.edu.utn.frba.dds.filtros.ParametroFiltro;
import ar.edu.utn.frba.dds.fuente.Fuente;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ColeccionService {

  private final RepositorioColecciones repositorio;
  private final Map<Long, Fuente> fuentesPorColeccion = new ConcurrentHashMap<>();

  public ColeccionService() {
    this(new RepositorioColeccionesJPA());
  }

  public ColeccionService(RepositorioColecciones repositorio) {
    this.repositorio = repositorio;
  }

  public List<Coleccion> listarColecciones() {
    List<Coleccion> colecciones = repositorio.buscarTodas();
    colecciones.forEach(this::adjuntarFuente);
    return colecciones;
  }

  public Optional<Coleccion> buscarPorId(Long id) {
    Coleccion coleccion = repositorio.buscarPorId(id);
    if (coleccion != null) {
      adjuntarFuente(coleccion);
    }
    return Optional.ofNullable(coleccion);
  }

  public Coleccion crearColeccion(String titulo, String descripcion,
                                  Fuente fuente, CriterioConsenso consenso,
                                  List<ParametroFiltro> criterios) {
    Coleccion coleccion = new Coleccion(titulo, descripcion, fuente, consenso);
    List<ParametroFiltro> persistibles = criterios == null ? List.of() : new ArrayList<>(criterios);
    persistibles.forEach(coleccion::agregarCriterioPertenencia);
    repositorio.guardar(coleccion);
    if (fuente != null && coleccion.getId() != null) {
      fuentesPorColeccion.put(coleccion.getId(), fuente);
    }
    return coleccion;
  }

  public void actualizarFuente(Coleccion coleccion, Fuente fuente) {
    if (coleccion == null || fuente == null) {
      return;
    }
    coleccion.setFuente(fuente);
    fuentesPorColeccion.put(coleccion.getId(), fuente);
  }

  public void sincronizarFuentes(Map<Long, Fuente> fuentes) {
    if (fuentes == null) {
      return;
    }
    fuentesPorColeccion.putAll(fuentes);
  }

  public Map<Long, Fuente> obtenerFuentesRegistradas() {
    return Map.copyOf(fuentesPorColeccion);
  }

  private void adjuntarFuente(Coleccion coleccion) {
    if (coleccion == null) {
      return;
    }
    Fuente fuenteRegistrada = fuentesPorColeccion.get(coleccion.getId());
    if (fuenteRegistrada != null) {
      coleccion.setFuente(fuenteRegistrada);
    }
  }
}
