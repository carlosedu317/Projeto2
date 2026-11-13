package projetoIntegrador.prototico.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
 
import projetoIntegrador.prototico.model.Pagamento;
import projetoIntegrador.prototico.model.TipoPagamento;
import projetoIntegrador.prototico.model.TipoStatusPagamento;
 
public record PagamentoResponse(
        Long id,
        Long pedidoId,
        BigDecimal valor,
        TipoPagamento tipoPagamento,
        TipoStatusPagamento status,
        LocalDateTime dataPagamento) {
 
    public static PagamentoResponse de(Pagamento p) {
        return new PagamentoResponse(
                p.getId(),
                p.getPedido().getId(),
                p.getValor(),
                p.getTipoPagamento(),
                p.getStatus(),
                p.getDataPagamento());
    }
}
 