package ar.edu.utn.frba.dds;

import ar.edu.utn.frba.dds.filtros.FiltroHechos;
import ar.edu.utn.frba.dds.gestor_solicitudes_eliminacion.GestorSolicitudesEliminacion;
import ar.edu.utn.frba.dds.hecho.Hecho;
import java.util.ArrayList;
import java.util.List;
import ar.edu.utn.frba.dds.fuente.estatica.FuenteEstatica;
import ar.edu.utn.frba.dds.fuente.estatica.LectorCsv;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class TestFuente {
  GestorSolicitudesEliminacion gestor;
  List<FiltroHechos> listaVacia;
  String pathTest;
  String pathVerdadero;

  @BeforeEach
  public void init(){
    gestor = GestorSolicitudesEliminacion.getInstancia();
    gestor.reset();
    listaVacia = new ArrayList<>();
    pathTest = "datasets/datasetPrueba.csv";
  }

  @Test
  public void laCantidadHechosDeUnaFuenteEstaticaConCsvManual() {
    FuenteEstatica fuenteEstatica = new FuenteEstatica(pathTest);

    Assertions.assertEquals(2000, fuenteEstatica.obtenerHechos().size());
  }

  @Test
  public void laCantidadHechosDeUnaFuenteEstaticaConCsvAutomatica() {
    FuenteEstatica fuenteEstatica = new FuenteEstatica(pathTest);

    Assertions.assertEquals(2000, fuenteEstatica.obtenerHechos().size());
    Assertions.assertEquals(2000, fuenteEstatica.obtenerHechos().size());
  }

  @Test
  public void verLosTitulosDelDataSet() {
    FuenteEstatica fuenteEstatica = new FuenteEstatica(pathTest);

    List<Hecho> hechos = fuenteEstatica.obtenerHechos();
    for(int i=0; i< hechos.size(); i++) {
      System.out.println(i +" " + hechos.get(i).getTitulo());
    }
  }

}
