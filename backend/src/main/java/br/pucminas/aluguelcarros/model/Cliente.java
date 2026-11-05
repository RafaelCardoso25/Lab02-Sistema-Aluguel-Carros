package br.pucminas.aluguelcarros.model;

import br.pucminas.aluguelcarros.exception.RegraNegocioException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clientes")
@PrimaryKeyJoinColumn(name = "id")
public class Cliente extends Usuario {

    public static final int MAXIMO_RENDIMENTOS = 3;

    @Column(nullable = false, unique = true)
    private String rg;

    @Column(nullable = false, unique = true)
    private String cpf;

    @Column(nullable = false)
    private String endereco;

    @Column(nullable = false)
    private String profissao;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Rendimento> rendimentos = new ArrayList<>();

    protected Cliente() {
    }

    public Cliente(String login, String senhaHash, String nome, String rg, String cpf, String endereco, String profissao) {
        super(login, senhaHash, nome);
        this.rg = rg;
        this.cpf = cpf;
        this.endereco = endereco;
        this.profissao = profissao;
    }

    @Override
    public Perfil getPerfil() {
        return Perfil.CLIENTE;
    }

    public void atualizarDados(String nome, String rg, String cpf, String endereco, String profissao) {
        alterarNome(nome);
        this.rg = rg;
        this.cpf = cpf;
        this.endereco = endereco;
        this.profissao = profissao;
    }

    public void definirRendimentos(List<Rendimento> novos) {
        if (novos.size() > MAXIMO_RENDIMENTOS) {
            throw new RegraNegocioException("Informe no máximo 3 rendimentos.");
        }
        rendimentos.clear();
        novos.forEach(rendimento -> {
            rendimento.vincular(this);
            rendimentos.add(rendimento);
        });
    }

    public BigDecimal getRendaTotal() {
        return rendimentos.stream().map(Rendimento::getValor).reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public String getRg() {
        return rg;
    }

    public String getCpf() {
        return cpf;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getProfissao() {
        return profissao;
    }

    public List<Rendimento> getRendimentos() {
        return rendimentos;
    }
}
