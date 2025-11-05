package ar.edu.utn.frba.dds.web.auth;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
import java.util.Optional;

public class AuthService {

  private final RepositorioUsuarios repoUsuarios;
  private final HexFormat hex = HexFormat.of();

  public AuthService(RepositorioUsuarios repoUsuarios) {
    this.repoUsuarios = repoUsuarios;
  }

  public UsuarioWeb registrar(String nombre, String email, String password, boolean admin) {
    if (email == null || email.isBlank()) {
      throw new IllegalArgumentException("El email es obligatorio");
    }
    if (password == null || password.length() < 6) {
      throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
    }
    if (repoUsuarios.buscarPorEmail(email).isPresent()) {
      throw new IllegalArgumentException("El email ya está registrado");
    }
    RolUsuario rol = admin ? RolUsuario.ADMIN : RolUsuario.CONTRIBUYENTE;
    UsuarioWeb usuario = new UsuarioWeb(nombre, email, hashPassword(password), rol);
    return repoUsuarios.guardar(usuario);
  }

  public Optional<UsuarioWeb> autenticar(String email, String password) {
    return repoUsuarios.buscarPorEmail(email)
        .filter(usuario -> usuario.getPasswordHash().equals(hashPassword(password)));
  }

  public Optional<UsuarioWeb> buscarPorId(Long id) {
    return repoUsuarios.buscarPorId(id);
  }

  public Optional<UsuarioWeb> buscarPorEmail(String email) {
    return repoUsuarios.buscarPorEmail(email);
  }

  private String hashPassword(String password) {
    try {
      MessageDigest digest = MessageDigest.getInstance("SHA-256");
      byte[] hashed = digest.digest(password.getBytes(StandardCharsets.UTF_8));
      return hex.formatHex(hashed);
    } catch (NoSuchAlgorithmException e) {
      throw new IllegalStateException("No se pudo inicializar el algoritmo de hash", e);
    }
  }
}
