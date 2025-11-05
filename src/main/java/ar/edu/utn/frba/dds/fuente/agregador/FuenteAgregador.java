package ar.edu.utn.frba.dds.fuente.agregador;

import ar.edu.utn.frba.dds.consensuador.Consensuador;
import ar.edu.utn.frba.dds.consensuador.CriterioConsenso;
import ar.edu.utn.frba.dds.hecho.Hecho;
import ar.edu.utn.frba.dds.fuente.Fuente;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;

@Getter
@Setter
@Entity
@DiscriminatorValue("AGREGADOR")
public class FuenteAgregador extends Fuente{

  @OneToMany(cascade = CascadeType.ALL)
  @JoinTable(
      name = "fuente_agregador_fuentes",
      joinColumns = @JoinColumn(name = "agregador_id"),
      inverseJoinColumns = @JoinColumn(name = "fuente_id")
  )
  private List<Fuente> fuentes = new ArrayList<>();
  @Transient
  private RepositorioHechos repoHechos;
  @Transient
  private Consensuador consensuador;

  public FuenteAgregador() {
    this.initTransient();
  }

  @PostLoad
  public void initTransient() {
    this.repoHechos = new RepositorioHechos();
    Consensuador.inicializar(Arrays.asList(CriterioConsenso.values()));
    this.consensuador = Consensuador.instancia();
  }

  @Override
  public List<Hecho> obtenerHechos() {
    return repoHechos.obtenerHechos();
  }

  public void agregarFuente(Fuente fuente){
    this.fuentes.add(fuente);
  }

  /* CRON */
  public void consultarFuentes(){
    List<Hecho> hechos = this.fuentes.stream().flatMap(fuente -> fuente.obtenerHechos().stream()).toList();
    repoHechos.actualizarHechos(hechos);
  }

  /* CRON */
  public void calcularConsensos(){
    Map<String, Set<CriterioConsenso>> consensos =
        (Map<String, Set<CriterioConsenso>>) consensuador.procesarConsensos(repoHechos.obtenerHechosParaConsenso(), fuentes.size());
    repoHechos.registrarConsensos(consensos);
  }
}
