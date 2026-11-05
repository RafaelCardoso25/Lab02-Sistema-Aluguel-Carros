package br.pucminas.aluguelcarros.model;

import jakarta.persistence.CascadeType;
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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "contratos")
public class Contrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "agente_id")
    private Agente agente;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoContrato tipo;

    @Column(nullable = false)
    private BigDecimal valor;

    @Column(name = "data_assinatura", nullable = false)
    private LocalDate dataAssinatura;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "contrato_credito_id")
    private ContratoCredito contratoCredito;

    protected Contrato() {
    }

    public Contrato(Pedido pedido, Agente agente, TipoContrato tipo, BigDecimal valor, ContratoCredito contratoCredito) {
        this.pedido = pedido;
        this.agente = agente;
        this.tipo = tipo;
        this.valor = valor;
        this.dataAssinatura = LocalDate.now();
        this.contratoCredito = contratoCredito;
    }

    public Long getId() {
        return id;
    }

    public Pedido getPedido() {
        return pedido;
    }

    public Agente getAgente() {
        return agente;
    }

    public TipoContrato getTipo() {
        return tipo;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public LocalDate getDataAssinatura() {
        return dataAssinatura;
    }

    public ContratoCredito getContratoCredito() {
        return contratoCredito;
    }
}
