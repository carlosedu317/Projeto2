package projetoIntegrador.prototico.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ItemPedidoRequest {

    @NotNull(message = "O produto é obrigatório")
    @Positive(message = "O ID do produto deve ser positivo")
    private Long produtoId;

    @Positive(message = "A quantidade deve ser maior que zero")
    private int quantidade;

    public Long getProdutoId() {
        return produtoId;
    }

    public void setProdutoId(Long produtoId) {
        this.produtoId = produtoId;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }
}