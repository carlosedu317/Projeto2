package projetoIntegrador.prototico.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import projetoIntegrador.prototico.model.Usuario;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    Optional<Usuario> findByEmail(String email);
}