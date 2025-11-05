package ar.edu.utn.frba.dds.fuente.estatica;

import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;

public class LectorCsv{
  private final String path;
  private InputStream inputStream;
  private Reader reader;
  private Iterator<Linea> iterator;

  public LectorCsv(String pathCsv) {
    this.path = pathCsv;
    this.inicializarLectura();
  }

  public Linea leerLinea() {
    if(!iterator.hasNext()) {
      reiniciar();
      return null;
    }

    return iterator.next();
  }

  public void cerrarLector() {
    try {
      reader.close();
    } catch (IOException io) {
      throw new RuntimeException("se cerro mal el Reader de lector csv automatico");
    }
  }

  public void reiniciar() {
    this.inicializarLectura();
  }

  private void inicializarLectura() {
    // Convierte el archivo en stream de bytes
    this.inputStream = getClass().getClassLoader().getResourceAsStream(path);
    if (inputStream == null)
      throw new RuntimeException("No se pudo encontrar el archivo: " + path);

    // Convierte el stream de bytes a stream de caracteres
    this.reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

    // Convierte una renglon del csv en la clase linea
    CsvToBean<Linea> csvToBean = new CsvToBeanBuilder<Linea>(reader).withType(Linea.class).withIgnoreLeadingWhiteSpace(true).build();

    // Devuelve un iterator para recorrer el csv
    this.iterator = csvToBean.iterator();
  }
}


