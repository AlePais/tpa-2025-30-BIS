package ar.edu.utn.frba.dds.fuente.estatica;

import com.opencsv.bean.CsvBindByName;
import com.opencsv.bean.CsvDate;
import java.time.LocalDate;
import ar.edu.utn.frba.dds.hecho.OrigenHecho;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Linea {
  @CsvBindByName(column = "Titulo")
  private String titulo;
  @CsvBindByName(column = "descripcion")
  private String descripcion;
  @CsvBindByName(column = "Categoria")
  private String categoria;
  @CsvBindByName(column = "Latitud")
  private String latitud;
  @CsvBindByName(column = "Longitud")
  private String longitud;
  @CsvBindByName(column = "Provincia")
  private String provincia;
  @CsvBindByName(column = "fecha")
  @CsvDate("yyyy-MM-dd")
  private LocalDateTime fechaHecho;
  private OrigenHecho origen;

}
