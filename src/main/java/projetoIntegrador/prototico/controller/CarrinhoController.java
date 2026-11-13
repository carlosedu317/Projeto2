package projetoIntegrador.prototico.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import projetoIntegrador.prototico.model.Carrinho;
import projetoIntegrador.prototico.model.Produto;
import projetoIntegrador.prototico.repository.CarrinhoRepository;
import projetoIntegrador.prototico.repository.ProdutoRepository;

@RestController
@RequestMapping("/api/carrinho")
@CrossOrigin(origins = "*")
public class CarrinhoController {

    private final CarrinhoRepository carrinhoRepository;
    private final ProdutoRepository produtoRepository;

    public CarrinhoController(
            CarrinhoRepository carrinhoRepository,
            ProdutoRepository produtoRepository) {

        this.carrinhoRepository = carrinhoRepository;
        this.produtoRepository = produtoRepository;
    }

    @GetMapping
    public List<Carrinho> listar() {
        return carrinhoRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Carrinho> buscarPorId(@PathVariable Long id) {
        return carrinhoRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Carrinho criar(@RequestBody @Valid Carrinho carrinho) {

        if (carrinho.getProduto() == null ||
                carrinho.getProduto().getId() == null) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Produto não informado."
            );
        }

        Produto produto = produtoRepository
                .findById(carrinho.getProduto().getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Produto não encontrado."
                ));

        if (carrinho.getQuantidade() > produto.getQuantidade()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Quantidade maior que o estoque disponível."
            );
        }

        carrinho.setProduto(produto);
        carrinho.setPrecoUni(produto.getPreco());
        carrinho.setSubTotal(
                produto.getPreco() * carrinho.getQuantidade()
        );

        return carrinhoRepository.save(carrinho);
    }

    @PutMapping("/{id}")
    public Carrinho atualizar(
            @PathVariable Long id,
            @RequestBody @Valid Carrinho carrinho) {

        Carrinho existente = carrinhoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Item não encontrado."
                ));

        if (carrinho.getQuantidade() < 1) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A quantidade deve ser maior que zero."
            );
        }

        Produto produto = produtoRepository
                .findById(existente.getProduto().getId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Produto não encontrado."
                ));

        if (carrinho.getQuantidade() > produto.getQuantidade()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Quantidade maior que o estoque disponível."
            );
        }

        existente.setProduto(produto);
        existente.setQuantidade(carrinho.getQuantidade());
        existente.setPrecoUni(produto.getPreco());
        existente.setSubTotal(
                produto.getPreco() * carrinho.getQuantidade()
        );

        return carrinhoRepository.save(existente);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {

        if (!carrinhoRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Item não encontrado."
            );
        }

        carrinhoRepository.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}