package ar.edu.utn.frba.dds.web.auth;

import io.github.flbulgarelli.jpa.extras.simple.WithSimplePersistenceUnit;
import java.util.Optional;
import javax.persistence.NoResultException;

public class RepositorioUsuarios implements WithSimplePersistenceUnit {

  public UsuarioWeb guardar(UsuarioWeb usuario) {
    return withTransaction(() -> {
      entityManager().persist(usuario);
      return usuario;
    });
  }

  public Optional<UsuarioWeb> buscarPorEmail(String email) {
    if (email == null) {
      return Optional.empty();
    }
    try {
      UsuarioWeb usuario = entityManager()
          .createQuery("SELECT u FROM UsuarioWeb u WHERE u.email = :email", UsuarioWeb.class)
          .setParameter("email", email.toLowerCase())
          .getSingleResult();
      return Optional.of(usuario);
    } catch (NoResultException e) {
      return Optional.empty();
    }
  }

  public Optional<UsuarioWeb> buscarPorId(Long id) {
    if (id == null) {
      return Optional.empty();
    }
    return Optional.ofNullable(entityManager().find(UsuarioWeb.class, id));
  }
}
