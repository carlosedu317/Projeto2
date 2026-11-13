package projetoIntegrador.prototico.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import projetoIntegrador.prototico.dto.PedidoRequest;
import projetoIntegrador.prototico.dto.ItemPedidoRequest;
import projetoIntegrador.prototico.model.Pedido;
import projetoIntegrador.prototico.model.ItemPedido;
import projetoIntegrador.prototico.model.TipoStatusPagamento;
import projetoIntegrador.prototico.model.Usuario;
import projetoIntegrador.prototico.model.Produto;
import projetoIntegrador.prototico.repository.PedidoRepository;
import projetoIntegrador.prototico.repository.UsuarioRepository;
import projetoIntegrador.prototico.repository.ProdutoRepository;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ProdutoRepository produtoRepository;

    @Transactional
    public Pedido cadastrar(PedidoRequest request) {

        Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        if (request.getItens() == null || request.getItens().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "O pedido precisa ter produtos");
        }

        Pedido pedido = new Pedido();

        pedido.setUsuario(usuario);
        pedido.setEnderecoEntrega(request.getEnderecoEntrega());
        pedido.setDataPedido(LocalDateTime.now());
        pedido.setStatusPagamento(TipoStatusPagamento.PENDENTE);

        BigDecimal valorTotal = BigDecimal.ZERO;

        for (ItemPedidoRequest itemRequest : request.getItens()) {

            if (itemRequest.getQuantidade() <= 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "Quantidade inválida");
            }

            Produto produto = produtoRepository.findById(itemRequest.getProdutoId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Produto não encontrado"));

            BigDecimal preco = BigDecimal.valueOf(produto.getPreco());

            BigDecimal subtotal = preco.multiply(
                    BigDecimal.valueOf(itemRequest.getQuantidade()));

            ItemPedido item = new ItemPedido();

            item.setPedido(pedido);
            item.setProduto(produto);
            item.setQuantidade(itemRequest.getQuantidade());
            item.setPrecoUnitario(preco);
            item.setSubtotal(subtotal);

            pedido.getItens().add(item);

            valorTotal = valorTotal.add(subtotal);
        }

        pedido.setValorTotal(valorTotal);

        return pedidoRepository.save(pedido);
    }

    @Transactional
    public Pedido confirmarPagamento(Long id) {

        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Pedido não encontrado"));

        if (pedido.getStatusPagamento() != TipoStatusPagamento.PENDENTE) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Este pedido não está pendente");
        }

        pedido.setStatusPagamento(TipoStatusPagamento.APROVADO);

        return pedidoRepository.save(pedido);
    }

    public java.util.List<Pedido> listar() {
        return pedidoRepository.findAll();
    }

    public Pedido buscarPorId(Long id) {

        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Pedido não encontrado"));
    }

    public void excluir(Long id) {

        if (!pedidoRepository.existsById(id)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Pedido não encontrado");
        }

        pedidoRepository.deleteById(id);
    }
}