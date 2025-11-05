package ar.edu.utn.frba.dds.estadistica.repositorio;

import ar.edu.utn.frba.dds.estadistica.Estadistica;
import ar.edu.utn.frba.dds.estadistica.TipoEstadistica;

import java.time.LocalDateTime;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;
import java.util.List;

public class RepositorioEstadisticasJPA implements RepositorioEstadisticas {

  @PersistenceContext
  private EntityManager em;

  @Override
  @Transactional
  public void guardar(Estadistica estadistica) {
    em.persist(estadistica);
  }

  @Override
  @Transactional
  public void guardarTodas(List<Estadistica> estadisticas) {
    for (Estadistica e : estadisticas) {
      em.persist(e);
    }
  }

  @Override
  public List<Estadistica> buscarPorTipo(TipoEstadistica tipo) {
    return em.createQuery(
            "SELECT e FROM Estadistica e WHERE e.tipo = :tipo", Estadistica.class)
        .setParameter("tipo", tipo)
        .getResultList();
  }

  @Override
  public List<Estadistica> buscarTodas() {
    return em.createQuery("SELECT e FROM Estadistica e", Estadistica.class)
        .getResultList();
  }

  @Override
  public List<Estadistica> buscarUltimaCorrida() {
    LocalDateTime ultimaFecha = em.createQuery(
        "SELECT MAX(e.fechaCalculo) FROM Estadistica e", LocalDateTime.class
    ).getSingleResult();

    return em.createQuery(
            "SELECT e FROM Estadistica e WHERE e.fechaCalculo = :fecha", Estadistica.class
        )
        .setParameter("fecha", ultimaFecha)
        .getResultList();
  }

  @Override
  @Transactional
  public void limpiar() {
    em.createQuery("DELETE FROM Estadistica").executeUpdate();
  }

  public void setEntityManager(EntityManager em) {
    this.em = em;
  }
}
