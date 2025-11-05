package ar.edu.utn.frba.dds.fuente.proxy;

import ar.edu.utn.frba.dds.hecho.Hecho;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

public class Conexion {

  private Queue<Hecho> hechos; //Hechos pendientes

  public Conexion() {
    hechos = new LinkedList<>();
  }

  /**
   * Devuelve un mapa con los atributos de un hecho, indexados por nombre de
   * atributo. Si el método retorna null, significa que no hay nuevos hechos por ahora.
   */
  public Map<String, Object> siguienteHecho(URL url, LocalDateTime fechaUltimaConsulta) {
    if (!hechos.isEmpty()) { //si hay hechos pendientes
      Hecho sigHecho = hechos.poll(); // saca el primer elemento de los Hechos pendientes
      Map<String, Object> hechoMap = new HashMap<>(); // crea un map para el hecho que saco.

      // actualiza los valores del map con el hecho que saco
      hechoMap.put("titulo", sigHecho.getTitulo());
      hechoMap.put("descripcion", sigHecho.getDescripcion());
      hechoMap.put("categoria", sigHecho.getCategoria());
      hechoMap.put("latitud", sigHecho.getLatitud());
      hechoMap.put("longitud", sigHecho.getLongitud());
      hechoMap.put("fecha", sigHecho.getFechaAcontecimiento());

      return hechoMap; // te devuelve un hecho pero mapeado, fuaaaaa
    }
    return null; // No hay elementos en la cola de hechos pendientes
  }

  /**
   * Agrega un hecho nuevo a la cola de hechos pendientes.
   */
  public void agregarHecho(Hecho hecho) {
    hechos.add(hecho);
  }

  /**
   * Devuelve la cantidad de hechos pendientes por entregar.
   */
  public int hechosPendientes() {
    return hechos.size();
  }
}
