package projetoIntegrador.prototico.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;

@Entity
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ManyToOne (e não OneToOne): permite uma nova tentativa de pagamento
    // se a anterior foi RECUSADA ou CANCELADA. A regra "só 1 pagamento ativo
    // por pedido" fica no PagamentoService.
    @NotNull
    @ManyToOne
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @NotNull
    private BigDecimal valor;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TipoPagamento tipoPagamento;

    @NotNull
    @Enumerated(EnumType.STRING)
    private TipoStatusPagamento status;

    // Preenchida pelo servidor quando o pagamento é aprovado.
    // Sem @FutureOrPresent: a data de um pagamento já feito é sempre passado.
    private LocalDateTime dataPagamento;

    public Pagamento() {
    }

    public Pagamento(Pedido pedido, BigDecimal valor, TipoPagamento tipoPagamento) {
        this.pedido = pedido;
        this.valor = valor;
        this.tipoPagamento = tipoPagamento;
        this.status = TipoStatusPagamento.PENDENTE;
    }

    public Long getId() {
        return id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public void setPedido(Pedido pedido) {
        this.pedido = pedido;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public TipoPagamento getTipoPagamento() {
        return tipoPagamento;
    }

    public void setTipoPagamento(TipoPagamento tipoPagamento) {
        this.tipoPagamento = tipoPagamento;
    }

    public TipoStatusPagamento getStatus() {
        return status;
    }

    public void setStatus(TipoStatusPagamento status) {
        this.status = status;
    }

    public LocalDateTime getDataPagamento() {
        return dataPagamento;
    }

    public void setDataPagamento(LocalDateTime dataPagamento) {
        this.dataPagamento = dataPagamento;
    }
}