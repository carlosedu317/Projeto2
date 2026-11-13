package projetoIntegrador.prototico.dto;

import jakarta.validation.constraints.NotNull;
import projetoIntegrador.prototico.model.TipoPagamento;
 

public record PagamentoRequest(
        @NotNull(message = "Informe o tipo de pagamento")
        TipoPagamento tipoPagamento) {
}