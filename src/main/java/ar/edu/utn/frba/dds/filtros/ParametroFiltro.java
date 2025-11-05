package ar.edu.utn.frba.dds.filtros;

import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import lombok.Getter;

@Entity
@Table
public class ParametroFiltro {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Getter
  @Enumerated(EnumType.STRING)
  private AtributoFiltro atributoFiltro;
  @Getter
  private String valor;

  public ParametroFiltro() {
  }

  public ParametroFiltro(AtributoFiltro atributoFiltro, String valor) {
    this.atributoFiltro = atributoFiltro;
    this.valor = valor;
  }
}
