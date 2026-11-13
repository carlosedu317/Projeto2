package projetoIntegrador.prototico.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import projetoIntegrador.prototico.model.*;

public interface UsuarioRepository extends JpaRepository < Usuario, Long> {
    
}
