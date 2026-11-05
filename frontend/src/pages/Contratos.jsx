import { useCallback } from 'react'
import { Link } from 'react-router-dom'
import Aviso from '../components/Aviso.jsx'
import Placa from '../components/Placa.jsx'
import { useDados } from '../hooks/useDados.js'
import { api } from '../services/api.js'
import { PERFIS, TIPOS_CONTRATO, formatarData, formatarMoeda } from '../utils/formato.js'

export default function Contratos() {
  const carregar = useCallback(() => api.listarContratos(), [])
  const { dados, erro, carregando } = useDados(carregar)
  const contratos = dados ?? []

  return (
    <>
      <h1>Contratos</h1>
      <Aviso>{erro}</Aviso>
      {!carregando && !erro && contratos.length === 0 && (
        <p className="vazio">Nenhum contrato ainda. Um contrato nasce quando um agente executa um pedido aprovado.</p>
      )}
      <ul className="grade-carros">
        {contratos.map((contrato) => (
          <li key={contrato.id} className="cartao cartao-carro">
            <Placa codigo={contrato.automovel.placa} />
            <h2>{TIPOS_CONTRATO[contrato.tipo].rotulo}</h2>
            <dl className="dados">
              <dt>Cliente</dt><dd>{contrato.clienteNome}</dd>
              <dt>Automóvel</dt><dd>{contrato.automovel.marca} {contrato.automovel.modelo}</dd>
              <dt>Valor</dt><dd>{formatarMoeda(contrato.valor)}</dd>
              <dt>Assinatura</dt><dd>{formatarData(contrato.dataAssinatura)}</dd>
              <dt>Executado por</dt><dd>{contrato.agenteNome}</dd>
              <dt>Proprietário</dt><dd>{contrato.automovel.proprietarioNome} ({PERFIS[contrato.automovel.proprietarioPerfil]})</dd>
              {contrato.credito && (
                <>
                  <dt>Crédito</dt><dd>{formatarMoeda(contrato.credito.valor)} em {contrato.credito.parcelas}x, {contrato.credito.taxaJurosMensal}% ao mês</dd>
                </>
              )}
            </dl>
            <div className="acoes">
              <Link to={`/pedidos/${contrato.pedidoId}`} className="botao botao-secundario">Ver pedido</Link>
            </div>
          </li>
        ))}
      </ul>
    </>
  )
}
