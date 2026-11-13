package projetoIntegrador.prototico.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import projetoIntegrador.prototico.model.Pedido;


public interface PedidoRepository extends JpaRepository< Pedido,Long >{
    
}
