package ar.edu.utn.frba.dds;

import ar.edu.utn.frba.dds.coleccion.Coleccion;
import ar.edu.utn.frba.dds.consensuador.CriterioConsenso;
import ar.edu.utn.frba.dds.filtros.AtributoFiltro;
import ar.edu.utn.frba.dds.filtros.ParametroFiltro;
import ar.edu.utn.frba.dds.fuente.Fuente;
import ar.edu.utn.frba.dds.fuente.estatica.FuenteEstatica;
import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;

public class Initializer2 implements WithSimplePersistenceUnit {

  public static void main(String[] args) {
    new Initializer2().run();
  }

  public void run() {

    withTransaction(() -> {
      // Crear la fuente de donde vendrán los hechos
      Fuente fuente = new FuenteEstatica("");

      // Crear la colección
      Coleccion coleccion = new Coleccion();
      coleccion.setTitulo("Colección de Prueba");
      coleccion.setDescripcion("Colección creada desde InitializerColeccion");
      coleccion.setFuente(fuente);
      coleccion.setCriterioConsenso(CriterioConsenso.MAYORIA_SIMPLE); // si aplica
      coleccion.setNavegacionCurada(true);

      // Crear un filtro persistible
      ParametroFiltro filtroPersistible = new ParametroFiltro(AtributoFiltro.CATEGORIA, "Noticias");

      // Crear un filtro no persistible
      ParametroFiltro filtroNoPersistible = new ParametroFiltro(AtributoFiltro.DESCRIPCION, "Urgente");

      // Agregar filtros a la colección
      coleccion.agregarCriterioPertenencia(filtroPersistible);
      coleccion.agregarCriterioPertenencia(filtroNoPersistible);


      // Persistir la colección
      entityManager().persist(coleccion);

      System.out.println("Colección y filtros persistibles guardados correctamente.");
    });
  }
}
