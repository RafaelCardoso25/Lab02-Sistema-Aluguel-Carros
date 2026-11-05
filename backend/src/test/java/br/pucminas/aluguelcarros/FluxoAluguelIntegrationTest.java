package br.pucminas.aluguelcarros;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.jayway.jsonpath.JsonPath;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
class FluxoAluguelIntegrationTest {

    private static final String CPF_1 = "52998224725";
    private static final String CPF_2 = "11144477735";
    private static final String CNPJ_EMPRESA = "11222333000181";
    private static final String CNPJ_BANCO = "11444777000161";
    private static final String INICIO = LocalDate.now().plusDays(5).toString();
    private static final String FIM = LocalDate.now().plusDays(10).toString();

    @Autowired
    private MockMvc mvc;

    @Test
    void rotasProtegidasExigemLogin() throws Exception {
        mvc.perform(get("/api/pedidos")).andExpect(status().isUnauthorized());
        mvc.perform(get("/api/automoveis")).andExpect(status().isUnauthorized());
    }

    @Test
    void loginRecusaSenhaIncorreta() throws Exception {
        cadastrarCliente("ana", CPF_1, "MG1");
        mvc.perform(json(post("/api/auth/login"), """
                {"login":"ana","senha":"senha-errada"}"""))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensagem").value("Usuário ou senha incorretos."));
    }

    @Test
    void crudDeCliente() throws Exception {
        String id = cadastrarCliente("ana", CPF_1, "MG1");
        String token = entrar("ana");

        mvc.perform(autenticado(get("/api/clientes/" + id), token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cpf").value(CPF_1))
                .andExpect(jsonPath("$.rendaTotal").value(5000.0));

        mvc.perform(autenticado(json(put("/api/clientes/" + id), """
                {"nome":"Ana Souza","rg":"MG1","cpf":"%s","endereco":"Rua B, 2","profissao":"Médica",
                 "rendimentos":[{"entidadeEmpregadora":"Hospital","valor":9000.00}]}""".formatted(CPF_1)), token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Ana Souza"))
                .andExpect(jsonPath("$.rendimentos.length()").value(1));

        mvc.perform(autenticado(delete("/api/clientes/" + id), token)).andExpect(status().isNoContent());
    }

    @Test
    void clienteAceitaNoMaximoTresRendimentos() throws Exception {
        String rendimento = "{\"entidadeEmpregadora\":\"X\",\"valor\":100}";
        mvc.perform(json(post("/api/clientes"), corpoCliente("ana", CPF_1, "MG1",
                        String.join(",", rendimento, rendimento, rendimento, rendimento))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.campos.rendimentos").value("Informe no máximo 3 rendimentos."));
    }

    @Test
    void clienteNaoAcessaCadastroNemPedidoDeOutroCliente() throws Exception {
        cadastrarAgente("locadora", CNPJ_EMPRESA, "EMPRESA");
        String automovel = cadastrarAutomovel(entrar("locadora"), "ABC1D23", "MAT1");
        String idAna = cadastrarCliente("ana", CPF_1, "MG1");
        cadastrarCliente("bia", CPF_2, "MG2");
        String tokenAna = entrar("ana");
        String tokenBia = entrar("bia");
        String pedido = criarPedido(tokenAna, automovel);

        mvc.perform(autenticado(get("/api/clientes/" + idAna), tokenBia)).andExpect(status().isForbidden());
        mvc.perform(autenticado(get("/api/pedidos/" + pedido), tokenBia)).andExpect(status().isForbidden());
    }

    @Test
    void fluxoCompletoComCreditoRegistraBancoComoProprietario() throws Exception {
        cadastrarAgente("locadora", CNPJ_EMPRESA, "EMPRESA");
        cadastrarAgente("banco", CNPJ_BANCO, "BANCO");
        cadastrarCliente("ana", CPF_1, "MG1");
        String tokenEmpresa = entrar("locadora");
        String tokenBanco = entrar("banco");
        String tokenCliente = entrar("ana");
        String automovel = cadastrarAutomovel(tokenEmpresa, "ABC1D23", "MAT1");
        String pedido = criarPedido(tokenCliente, automovel);

        mvc.perform(autenticado(get("/api/pedidos/" + pedido), tokenCliente))
                .andExpect(jsonPath("$.status").value("EM_ANALISE"));

        mvc.perform(autenticado(json(post("/api/pedidos/" + pedido + "/avaliacao"), """
                {"aprovado":true,"parecer":"Renda compatível"}"""), tokenCliente))
                .andExpect(status().isForbidden());

        contratar(tokenEmpresa, pedido, "ALUGUEL_SIMPLES", null).andExpect(status().isConflict());

        aprovar(tokenBanco, pedido);

        mvc.perform(autenticado(json(put("/api/pedidos/" + pedido), corpoPedido(automovel)), tokenCliente))
                .andExpect(status().isConflict());

        contratar(tokenEmpresa, pedido, "ALUGUEL_COM_CREDITO", credito()).andExpect(status().isConflict());
        contratar(tokenBanco, pedido, "ALUGUEL_SIMPLES", null).andExpect(status().isConflict());
        contratar(tokenBanco, pedido, "ALUGUEL_COM_CREDITO", null).andExpect(status().isConflict());

        contratar(tokenBanco, pedido, "ALUGUEL_COM_CREDITO", credito())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.credito.parcelas").value(12))
                .andExpect(jsonPath("$.automovel.proprietarioPerfil").value("BANCO"));

        mvc.perform(autenticado(get("/api/pedidos/" + pedido), tokenCliente))
                .andExpect(jsonPath("$.status").value("CONTRATADO"))
                .andExpect(jsonPath("$.contrato.tipo").value("ALUGUEL_COM_CREDITO"));

        mvc.perform(autenticado(post("/api/pedidos/" + pedido + "/cancelamento"), tokenCliente))
                .andExpect(status().isConflict());

        mvc.perform(autenticado(json(post("/api/pedidos"), corpoPedido(automovel)), tokenCliente))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensagem").value("Este automóvel já tem contrato nesse período."));
    }

    @Test
    void contratoComCompraTornaClienteProprietario() throws Exception {
        cadastrarAgente("locadora", CNPJ_EMPRESA, "EMPRESA");
        cadastrarCliente("ana", CPF_1, "MG1");
        String tokenEmpresa = entrar("locadora");
        String tokenCliente = entrar("ana");
        String automovel = cadastrarAutomovel(tokenEmpresa, "ABC1D23", "MAT1");
        String pedido = criarPedido(tokenCliente, automovel);
        aprovar(tokenEmpresa, pedido);

        contratar(tokenEmpresa, pedido, "ALUGUEL_COM_COMPRA", null)
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.automovel.proprietarioPerfil").value("CLIENTE"))
                .andExpect(jsonPath("$.automovel.disponivel").value(false));
    }

    @Test
    void clienteCancelaEAgenteModificaPedido() throws Exception {
        cadastrarAgente("locadora", CNPJ_EMPRESA, "EMPRESA");
        cadastrarCliente("ana", CPF_1, "MG1");
        String tokenEmpresa = entrar("locadora");
        String tokenCliente = entrar("ana");
        String automovel = cadastrarAutomovel(tokenEmpresa, "ABC1D23", "MAT1");
        String pedido = criarPedido(tokenCliente, automovel);

        mvc.perform(autenticado(json(put("/api/pedidos/" + pedido), corpoPedido(automovel)), tokenEmpresa))
                .andExpect(status().isOk());

        mvc.perform(autenticado(post("/api/pedidos/" + pedido + "/cancelamento"), tokenCliente))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELADO"));

        mvc.perform(autenticado(json(post("/api/pedidos/" + pedido + "/avaliacao"), """
                {"aprovado":true,"parecer":"ok"}"""), tokenEmpresa))
                .andExpect(status().isConflict());
    }

    @Test
    void clienteNaoCadastraAutomovel() throws Exception {
        cadastrarCliente("ana", CPF_1, "MG1");
        mvc.perform(autenticado(json(post("/api/automoveis"), corpoAutomovel("ABC1D23", "MAT1")), entrar("ana")))
                .andExpect(status().isForbidden());
    }

    private ResultActions contratar(String token, String pedido, String tipo, String credito) throws Exception {
        String corpo = """
                {"pedidoId":%s,"tipo":"%s","valor":1500.00,"credito":%s}""".formatted(pedido, tipo, credito);
        return mvc.perform(autenticado(json(post("/api/contratos"), corpo), token));
    }

    private void aprovar(String token, String pedido) throws Exception {
        mvc.perform(autenticado(json(post("/api/pedidos/" + pedido + "/avaliacao"), """
                {"aprovado":true,"parecer":"Renda compatível"}"""), token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APROVADO"));
    }

    private String credito() {
        return """
                {"valor":1500.00,"taxaJurosMensal":1.50,"parcelas":12}""";
    }

    private String criarPedido(String token, String automovel) throws Exception {
        return id(mvc.perform(autenticado(json(post("/api/pedidos"), corpoPedido(automovel)), token))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString());
    }

    private String corpoPedido(String automovel) {
        return """
                {"automovelId":%s,"dataInicio":"%s","dataFim":"%s"}""".formatted(automovel, INICIO, FIM);
    }

    private String cadastrarAutomovel(String token, String placa, String matricula) throws Exception {
        return id(mvc.perform(autenticado(json(post("/api/automoveis"), corpoAutomovel(placa, matricula)), token))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString());
    }

    private String corpoAutomovel(String placa, String matricula) {
        return """
                {"matricula":"%s","ano":2024,"marca":"Fiat","modelo":"Pulse","placa":"%s"}""".formatted(matricula, placa);
    }

    private String cadastrarCliente(String login, String cpf, String rg) throws Exception {
        String rendimento = "{\"entidadeEmpregadora\":\"Empresa X\",\"valor\":5000.00}";
        return id(mvc.perform(json(post("/api/clientes"), corpoCliente(login, cpf, rg, rendimento)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString());
    }

    private String corpoCliente(String login, String cpf, String rg, String rendimentos) {
        return """
                {"login":"%s","senha":"senha-forte-1","nome":"%s","rg":"%s","cpf":"%s",
                 "endereco":"Rua A, 1","profissao":"Analista","rendimentos":[%s]}""".formatted(login, login, rg, cpf, rendimentos);
    }

    private void cadastrarAgente(String login, String cnpj, String tipo) throws Exception {
        mvc.perform(json(post("/api/agentes"), """
                {"login":"%s","senha":"senha-forte-1","nome":"%s","cnpj":"%s","tipo":"%s"}""".formatted(login, login, cnpj, tipo)))
                .andExpect(status().isCreated());
    }

    private String entrar(String login) throws Exception {
        String resposta = mvc.perform(json(post("/api/auth/login"), """
                {"login":"%s","senha":"senha-forte-1"}""".formatted(login)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(resposta, "$.token");
    }

    private String id(String resposta) {
        return String.valueOf((Object) JsonPath.read(resposta, "$.id"));
    }

    private MockHttpServletRequestBuilder json(MockHttpServletRequestBuilder requisicao, String corpo) {
        return requisicao.contentType(MediaType.APPLICATION_JSON).content(corpo);
    }

    private MockHttpServletRequestBuilder autenticado(MockHttpServletRequestBuilder requisicao, String token) {
        return requisicao.header("Authorization", "Bearer " + token);
    }
}
