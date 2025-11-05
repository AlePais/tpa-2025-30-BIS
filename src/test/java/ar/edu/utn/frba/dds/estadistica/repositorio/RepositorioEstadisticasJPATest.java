package ar.edu.utn.frba.dds.estadistica.repositorio;

import static org.junit.jupiter.api.Assertions.*;

import ar.edu.utn.frba.dds.estadistica.Estadistica;
import ar.edu.utn.frba.dds.estadistica.TipoEstadistica;
import org.junit.jupiter.api.*;
import javax.persistence.*;
import java.util.List;

class RepositorioEstadisticasJPATest {

  private static EntityManagerFactory emf;
  private EntityManager em;
  private RepositorioEstadisticasJPA repo;

  @BeforeAll
  static void init() {
    emf = Persistence.createEntityManagerFactory("simple-persistence-unit");
  }

  @AfterAll
  static void tearDown() {
    if (emf != null) {
      emf.close();
    }
  }

  @BeforeEach
  void setUp() {
    em = emf.createEntityManager();
    repo = new RepositorioEstadisticasJPA();
    // Inyectamos el EntityManager (si usás @PersistenceContext normalmente lo hace el contenedor)
    repo.setEntityManager(em);
  }

  @AfterEach
  void cleanUp() {
    if (em != null) {
      em.close();
    }
  }

  @Test
  void testGuardarYBuscarPorTipo() {
    Estadistica e1 = new Estadistica(TipoEstadistica.HECHOS_POR_PROVINCIA, "CORDOBA", 5L);
    Estadistica e2 = new Estadistica(TipoEstadistica.HECHOS_POR_PROVINCIA, "BUENOS AIRES", 3L);
    Estadistica e3 = new Estadistica(TipoEstadistica.HECHOS_POR_CATEGORIA, "DELITO", 10L);

    em.getTransaction().begin();
    repo.guardar(e1);
    repo.guardar(e2);
    repo.guardar(e3);
    em.getTransaction().commit();

    List<Estadistica> porProvincia = repo.buscarPorTipo(TipoEstadistica.HECHOS_POR_PROVINCIA);
    assertEquals(2, porProvincia.size());

    List<Estadistica> porCategoria = repo.buscarPorTipo(TipoEstadistica.HECHOS_POR_CATEGORIA);
    assertEquals(1, porCategoria.size());
  }

  @Test
  void testBuscarTodasYLuegoLimpiar() {
    Estadistica e1 = new Estadistica(TipoEstadistica.HECHOS_POR_HORA, "10", 4L);
    Estadistica e2 = new Estadistica(TipoEstadistica.SOLICITUDES_SPAM, "Total", 2L);

    em.getTransaction().begin();
    repo.guardar(e1);
    repo.guardar(e2);
    em.getTransaction().commit();

    List<Estadistica> todas = repo.buscarTodas();
    assertEquals(2, todas.size());

    em.getTransaction().begin();
    repo.limpiar();
    em.getTransaction().commit();

    List<Estadistica> despues = repo.buscarTodas();
    assertTrue(despues.isEmpty());
  }
}
