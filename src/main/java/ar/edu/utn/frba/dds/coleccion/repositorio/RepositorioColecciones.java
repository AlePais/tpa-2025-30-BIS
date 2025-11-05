package ar.edu.utn.frba.dds.coleccion.repositorio;

import ar.edu.utn.frba.dds.coleccion.Coleccion;
import java.util.List;

public interface RepositorioColecciones {

  void guardar(Coleccion coleccion);

  Coleccion buscarPorId(Long id);

  List<Coleccion> buscarTodas();

  Coleccion actualizar(Coleccion coleccion);

  void eliminar(Coleccion coleccion);
}