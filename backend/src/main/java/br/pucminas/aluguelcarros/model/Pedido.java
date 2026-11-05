package br.pucminas.aluguelcarros.model;

import br.pucminas.aluguelcarros.exception.RegraNegocioException;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "automovel_id")
    private Automovel automovel;

    @Column(name = "data_inicio", nullable = false)
    private LocalDate dataInicio;

    @Column(name = "data_fim", nullable = false)
    private LocalDate dataFim;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPedido status;

    private String parecer;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "avaliado_por_id")
    private Agente avaliadoPor;

    @Column(name = "criado_em", nullable = false)
    private Instant criadoEm;

    @Column(name = "atualizado_em", nullable = false)
    private Instant atualizadoEm;

    protected Pedido() {
    }

    public Pedido(Cliente cliente, Automovel automovel, LocalDate dataInicio, LocalDate dataFim) {
        validarPeriodo(dataInicio, dataFim);
        this.cliente = cliente;
        this.automovel = automovel;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
        this.status = StatusPedido.EM_ANALISE;
        this.criadoEm = Instant.now();
        this.atualizadoEm = this.criadoEm;
    }

    public void modificarPeloCliente(Automovel automovel, LocalDate dataInicio, LocalDate dataFim) {
        if (status != StatusPedido.EM_ANALISE) {
            throw new RegraNegocioException("O cliente só pode modificar pedidos em análise.");
        }
        aplicarModificacao(automovel, dataInicio, dataFim);
    }

    public void modificarPeloAgente(Automovel automovel, LocalDate dataInicio, LocalDate dataFim) {
        if (status != StatusPedido.EM_ANALISE && status != StatusPedido.APROVADO) {
            throw new RegraNegocioException("O agente só pode modificar pedidos em análise ou aprovados.");
        }
        aplicarModificacao(automovel, dataInicio, dataFim);
    }

    public void avaliar(Agente agente, boolean aprovado, String parecer) {
        if (status != StatusPedido.EM_ANALISE) {
            throw new RegraNegocioException("Somente pedidos em análise podem ser avaliados.");
        }
        this.status = aprovado ? StatusPedido.APROVADO : StatusPedido.REPROVADO;
        this.avaliadoPor = agente;
        this.parecer = parecer;
    }

    public void cancelar() {
        if (status != StatusPedido.EM_ANALISE && status != StatusPedido.APROVADO) {
            throw new RegraNegocioException("Somente pedidos em análise ou aprovados podem ser cancelados.");
        }
        this.status = StatusPedido.CANCELADO;
    }

    public void registrarContrato() {
        if (status != StatusPedido.APROVADO) {
            throw new RegraNegocioException("Somente pedidos aprovados podem virar contrato.");
        }
        this.status = StatusPedido.CONTRATADO;
    }

    @PreUpdate
    void aoAtualizar() {
        this.atualizadoEm = Instant.now();
    }

    private void aplicarModificacao(Automovel automovel, LocalDate dataInicio, LocalDate dataFim) {
        validarPeriodo(dataInicio, dataFim);
        this.automovel = automovel;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
    }

    private static void validarPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        if (dataFim.isBefore(dataInicio)) {
            throw new RegraNegocioException("A data de devolução não pode ser anterior à data de retirada.");
        }
    }

    public Long getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Automovel getAutomovel() {
        return automovel;
    }

    public LocalDate getDataInicio() {
        return dataInicio;
    }

    public LocalDate getDataFim() {
        return dataFim;
    }

    public StatusPedido getStatus() {
        return status;
    }

    public String getParecer() {
        return parecer;
    }

    public Agente getAvaliadoPor() {
        return avaliadoPor;
    }

    public Instant getCriadoEm() {
        return criadoEm;
    }

    public Instant getAtualizadoEm() {
        return atualizadoEm;
    }
}
