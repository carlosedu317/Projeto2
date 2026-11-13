package projetoIntegrador.prototico.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import projetoIntegrador.prototico.model.Pagamento;
import projetoIntegrador.prototico.service.PagamentoService;

@RestController 
@RequestMapping ("/api/pagamentos")
public class PagamentoController {

    @Autowired 
    private PagamentoService service;

    @PostMapping 
    public Pagamento cadastrar(@RequestBody Pagamento pagamento) {
        return service.cadastrar(pagamento);
    }

    @GetMapping 
    public List<Pagamento> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public Pagamento buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id);
    }

    @GetMapping("/pedido/{pedidoId}")
    public Pagamento buscarPorPedido(@PathVariable Long pedidoId) {
        return service.buscarPorPedido(pedidoId);
    }

    @PutMapping ("/{id}")
    public Pagamento atualizar(
            @PathVariable Long id,
            @RequestBody Pagamento pagamento) {
        return service.atualizar(id, pagamento);
    }

    @DeleteMapping ("/{id}")
    public void excluir(@PathVariable Long id) {
        service.excluir(id);
    }
}
