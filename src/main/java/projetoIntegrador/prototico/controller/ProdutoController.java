
package projetoIntegrador.prototico.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import projetoIntegrador.prototico.model.Produto;
import projetoIntegrador.prototico.service.ProdutoService;

@RestController
@RequestMapping("/api/produtos")
@CrossOrigin(origins = "*")
public class ProdutoController {

    private final ProdutoService service;

    public ProdutoController(ProdutoService service) {
        this.service = service;
    }

    // Listar produtos
    @GetMapping
    public List<Produto> listar() {
        return service.listar();
    }

    // Buscar produto pelo ID
    @GetMapping("/{id}")
    public ResponseEntity<Produto> buscarPorId(
            @PathVariable Long id) {

        return ResponseEntity.ok(service.buscarPorId(id));
    }

    // Cadastrar produto
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Produto cadastrar(
            @RequestBody @Valid Produto produto) {

        return service.cadastrar(produto);
    }

    // Atualizar produto
    @PutMapping("/{id}")
    public ResponseEntity<Produto> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid Produto produto) {

        return ResponseEntity.ok(
                service.atualizar(id, produto)
        );
    }

    // Excluir produto
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {

        service.excluir(id);
    }
}
