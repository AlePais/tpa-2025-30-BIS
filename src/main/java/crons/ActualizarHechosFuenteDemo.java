package crons;

import ar.edu.utn.frba.dds.fuente.proxy.FuenteDemo;
import ar.edu.utn.frba.dds.fuente.proxy.Conexion;
import java.net.URL;

public class ActualizarHechosFuenteDemo {

  public static void main(String[] args) throws Exception{

    FuenteDemo demo = new FuenteDemo(new Conexion(), new URL("http://test.com"));

    demo.consultarHechosNuevos();
    demo.actualizarFechaConsultada();

    System.out.println("se actualizo el demo");

    Thread.sleep(3_000);
  }

}
