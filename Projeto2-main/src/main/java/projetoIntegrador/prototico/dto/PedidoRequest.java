package projetoIntegrador.prototico.dto;

import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;


public class PedidoRequest {

    @NotNull(message = "O usuário é obrigatório")
    private Long usuarioId;

    @NotBlank(message = "O endereço é obrigatório")
    private String enderecoEntrega;

    @NotEmpty(message = "O pedido precisa ter produtos")
    @Valid
    private List<ItemPedidoRequest> itens;

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getEnderecoEntrega() {
        return enderecoEntrega;
    }

    public void setEnderecoEntrega(String enderecoEntrega) {
        this.enderecoEntrega = enderecoEntrega;
    }

    public List<ItemPedidoRequest> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedidoRequest> itens) {
        this.itens = itens;
    }
}