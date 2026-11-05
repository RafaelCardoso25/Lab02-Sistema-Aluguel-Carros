import { useCallback, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import Aviso from '../components/Aviso.jsx'
import FormularioContrato from '../components/FormularioContrato.jsx'
import FormularioPedido from '../components/FormularioPedido.jsx'
import Placa from '../components/Placa.jsx'
import RotaPedido from '../components/RotaPedido.jsx'
import SeloStatus from '../components/SeloStatus.jsx'
import { useAuth } from '../hooks/useAuth.jsx'
import { useDados } from '../hooks/useDados.js'
import { api } from '../services/api.js'
import { PERFIS, TIPOS_CONTRATO, formatarCpf, formatarData, formatarMoeda } from '../utils/formato.js'

function FormularioAvaliacao({ onEnviar }) {
  const [parecer, setParecer] = useState('')
  const [erro, setErro] = useState(null)
  const [enviando, setEnviando] = useState(false)

  const avaliar = async (aprovado) => {
    if (!parecer.trim()) {
      setErro('Escreva o parecer financeiro antes de decidir.')
      return
    }
    setEnviando(true)
    setErro(null)
    try {
      await onEnviar({ aprovado, parecer })
    } catch (falha) {
      setErro(falha.message)
      setEnviando(false)
    }
  }

  return (
    <div className="formulario">
      <Aviso>{erro}</Aviso>
      <div className="campo">
        <label htmlFor="parecer">Parecer financeiro</label>
        <textarea id="parecer" rows="3" maxLength="500" value={parecer} onChange={(e) => setParecer(e.target.value)} />
      </div>
      <div className="acoes">
        <button type="button" className="botao botao-primario" disabled={enviando} onClick={() => avaliar(true)}>Aprovar pedido</button>
        <button type="button" className="botao botao-perigo" disabled={enviando} onClick={() => avaliar(false)}>Reprovar pedido</button>
      </div>
    </div>
  )
}

export default function DetalhePedido() {
  const { id } = useParams()
  const { sessao, ehCliente, ehAgente } = useAuth()
  const carregar = useCallback(() => api.buscarPedido(id), [id])
  const { dados: pedido, setDados, erro, carregando, recarregar } = useDados(carregar)
  const [editando, setEditando] = useState(false)
  const [erroAcao, setErroAcao] = useState(null)

  if (carregando && !pedido) return <p className="carregando">Carregando…</p>
  if (erro && !pedido) return <Aviso>{erro}</Aviso>

  const { status, cliente, automovel, contrato } = pedido
  const clientePodeModificar = ehCliente && status === 'EM_ANALISE'
  const agentePodeModificar = ehAgente && (status === 'EM_ANALISE' || status === 'APROVADO')
  const podeCancelar = ehCliente && (status === 'EM_ANALISE' || status === 'APROVADO')

  const cancelar = async () => {
    if (!window.confirm('Cancelar este pedido? Essa ação não pode ser desfeita.')) return
    setErroAcao(null)
    try {
      setDados(await api.cancelarPedido(id))
    } catch (falha) {
      setErroAcao(falha.message)
    }
  }

  const modificar = async (dados) => {
    setDados(await api.modificarPedido(id, dados))
    setEditando(false)
  }

  const avaliar = async (dados) => setDados(await api.avaliarPedido(id, dados))

  const executar = async (dados) => {
    await api.executarContrato({ ...dados, pedidoId: pedido.id })
    await recarregar()
  }

  return (
    <>
      <p className="migalha"><Link to="/pedidos">Pedidos</Link> / Pedido {pedido.id}</p>
      <div className="cabecalho-pagina">
        <h1>{automovel.marca} {automovel.modelo}</h1>
        <SeloStatus status={status} />
      </div>

      <section className="painel-rota">
        <RotaPedido status={status} avaliado={Boolean(pedido.avaliadoPorNome)} />
      </section>

      <Aviso>{erroAcao}</Aviso>

      <div className="detalhe-grade">
        <section className="cartao">
          <h2>Aluguel</h2>
          {editando ? (
            <FormularioPedido
              inicial={{ automovelId: automovel.id, dataInicio: pedido.dataInicio, dataFim: pedido.dataFim }}
              rotuloEnviar="Salvar alterações"
              onEnviar={modificar}
              onCancelar={() => setEditando(false)}
            />
          ) : (
            <>
              <Placa codigo={automovel.placa} />
              <dl className="dados">
                <dt>Ano</dt><dd>{automovel.ano}</dd>
                <dt>Matrícula</dt><dd>{automovel.matricula}</dd>
                <dt>Retirada</dt><dd>{formatarData(pedido.dataInicio)}</dd>
                <dt>Devolução</dt><dd>{formatarData(pedido.dataFim)}</dd>
                <dt>Proprietário</dt><dd>{automovel.proprietarioNome} ({PERFIS[automovel.proprietarioPerfil]})</dd>
              </dl>
              <div className="acoes">
                {(clientePodeModificar || agentePodeModificar) && (
                  <button type="button" className="botao botao-secundario" onClick={() => setEditando(true)}>Modificar pedido</button>
                )}
                {podeCancelar && (
                  <button type="button" className="botao botao-perigo" onClick={cancelar}>Cancelar pedido</button>
                )}
              </div>
            </>
          )}
        </section>

        {ehAgente && (
          <section className="cartao">
            <h2>Contratante</h2>
            <dl className="dados">
              <dt>Nome</dt><dd>{cliente.nome}</dd>
              <dt>CPF</dt><dd>{formatarCpf(cliente.cpf)}</dd>
              <dt>RG</dt><dd>{cliente.rg}</dd>
              <dt>Endereço</dt><dd>{cliente.endereco}</dd>
              <dt>Profissão</dt><dd>{cliente.profissao}</dd>
            </dl>
            <h3>Rendimentos</h3>
            {cliente.rendimentos.length === 0 && <p className="vazio-curto">Nenhum rendimento informado.</p>}
            <ul className="lista-rendimentos">
              {cliente.rendimentos.map((r, indice) => (
                <li key={indice}><span>{r.entidadeEmpregadora}</span><span>{formatarMoeda(r.valor)}</span></li>
              ))}
              <li className="total"><span>Total mensal</span><span>{formatarMoeda(cliente.rendaTotal)}</span></li>
            </ul>
          </section>
        )}

        {pedido.parecer && (
          <section className="cartao">
            <h2>Parecer financeiro</h2>
            <p>{pedido.parecer}</p>
            <p className="assinatura">Avaliado por {pedido.avaliadoPorNome}</p>
          </section>
        )}

        {contrato && (
          <section className="cartao">
            <h2>Contrato</h2>
            <dl className="dados">
              <dt>Tipo</dt><dd>{TIPOS_CONTRATO[contrato.tipo].rotulo}</dd>
              <dt>Valor</dt><dd>{formatarMoeda(contrato.valor)}</dd>
              <dt>Assinatura</dt><dd>{formatarData(contrato.dataAssinatura)}</dd>
              <dt>Executado por</dt><dd>{contrato.agenteNome}</dd>
              {contrato.credito && (
                <>
                  <dt>Banco</dt><dd>{contrato.credito.bancoNome}</dd>
                  <dt>Crédito</dt><dd>{formatarMoeda(contrato.credito.valor)}</dd>
                  <dt>Juros</dt><dd>{contrato.credito.taxaJurosMensal}% ao mês</dd>
                  <dt>Parcelas</dt><dd>{contrato.credito.parcelas}</dd>
                </>
              )}
            </dl>
          </section>
        )}

        {ehAgente && status === 'EM_ANALISE' && !editando && (
          <section className="cartao">
            <h2>Avaliar pedido</h2>
            <FormularioAvaliacao onEnviar={avaliar} />
          </section>
        )}

        {ehAgente && status === 'APROVADO' && !editando && (
          <section className="cartao">
            <h2>Executar contrato</h2>
            <FormularioContrato perfil={sessao.perfil} onEnviar={executar} />
          </section>
        )}
      </div>
    </>
  )
}
