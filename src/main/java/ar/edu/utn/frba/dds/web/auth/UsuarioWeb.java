package ar.edu.utn.frba.dds.web.auth;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "UsuariosWeb")
public class UsuarioWeb {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String nombre;

  @Column(nullable = false, unique = true)
  private String email;

  @Column(nullable = false, name = "password_hash")
  private String passwordHash;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private RolUsuario rol;

  public UsuarioWeb() {
  }

  public UsuarioWeb(String nombre, String email, String passwordHash, RolUsuario rol) {
    if (nombre == null || nombre.isBlank()) {
      throw new IllegalArgumentException("El nombre no puede estar vacío");
    }
    if (email == null || email.isBlank()) {
      throw new IllegalArgumentException("El email no puede estar vacío");
    }
    if (passwordHash == null || passwordHash.isBlank()) {
      throw new IllegalArgumentException("La contraseña no puede estar vacía");
    }
    this.nombre = nombre;
    this.email = email.toLowerCase();
    this.passwordHash = passwordHash;
    this.rol = rol == null ? RolUsuario.CONTRIBUYENTE : rol;
  }

  public boolean esAdmin() {
    return RolUsuario.ADMIN.equals(this.rol);
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getNombre() {
    return nombre;
  }

  public void setNombre(String nombre) {
    this.nombre = nombre;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public String getPasswordHash() {
    return passwordHash;
  }

  public void setPasswordHash(String passwordHash) {
    this.passwordHash = passwordHash;
  }

  public RolUsuario getRol() {
    return rol;
  }

  public void setRol(RolUsuario rol) {
    this.rol = rol;
  }
}
