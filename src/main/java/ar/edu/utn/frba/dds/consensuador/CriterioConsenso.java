package ar.edu.utn.frba.dds.consensuador;

import ar.edu.utn.frba.dds.hecho.Hecho;
import java.util.List;

public enum CriterioConsenso {
  ABSOLUTO {
    public boolean esConsensuado(Hecho hecho, List<Hecho> hechos, int numeroFuentes) {
      if (numeroFuentes < 2) return false;
      long fuentesConHecho = hechos.stream()
          .filter(h -> hecho.equalsTotal(h))
          .count();

      return fuentesConHecho == numeroFuentes;
    }
  },
  MAYORIA_SIMPLE{
    public boolean esConsensuado(Hecho hecho, List<Hecho> hechos, int numeroFuentes) {

      if (numeroFuentes < 2) return false;
      long coincidencias = hechos.stream()
          .filter(h -> hecho.equalsTotal(h))
          .count();

      return coincidencias >= (numeroFuentes + 1) / 2; // mitades redondeando para arriba
    }
  },
  MULTIPLES_MENCIONES{
    public boolean esConsensuado(Hecho hecho, List<Hecho> hechos, int numeroFuentes) {
      final int MIN_MENCIONES = 2;

      if (numeroFuentes < 2) return false;
      // Contar hechos exactamente iguales (título y atributos)
      long iguales = hechos.stream()
          .filter(h -> hecho.equalsTotal(h))
          .count();

      // Contar hechos con el mismo título pero atributos diferentes
      boolean hayConflicto = hechos.stream()
          .anyMatch(h -> h.equals(hecho) && !hecho.equalsTotal(h));

      return iguales >= MIN_MENCIONES && !hayConflicto;
    }
  };

  public abstract boolean esConsensuado(Hecho hecho, List<Hecho> hechos, int numeroFuentes);

}
