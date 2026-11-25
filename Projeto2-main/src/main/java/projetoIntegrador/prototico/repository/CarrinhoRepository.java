package projetoIntegrador.prototico.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import projetoIntegrador.prototico.model.Carrinho;


public interface CarrinhoRepository extends JpaRepository< Carrinho,Long > {
    
}
