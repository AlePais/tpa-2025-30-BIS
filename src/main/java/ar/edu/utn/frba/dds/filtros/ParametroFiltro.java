package ar.edu.utn.frba.dds.filtros;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "ParametrosFiltro")
public class ParametroFiltro {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  private AtributoFiltro atributoFiltro;
  private String valor;

  public ParametroFiltro() {
  }

  public ParametroFiltro(AtributoFiltro atributoFiltro, String valor) {
    this.atributoFiltro = atributoFiltro;
    this.valor = valor;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public AtributoFiltro getAtributoFiltro() {
    return atributoFiltro;
  }

  public void setAtributoFiltro(AtributoFiltro atributoFiltro) {
    this.atributoFiltro = atributoFiltro;
  }

  public String getValor() {
    return valor;
  }

  public void setValor(String valor) {
    this.valor = valor;
  }
}
