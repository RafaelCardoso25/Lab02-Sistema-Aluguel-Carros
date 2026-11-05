import { useCallback, useState } from 'react'
import { Link } from 'react-router-dom'
import Aviso from '../components/Aviso.jsx'
import Placa from '../components/Placa.jsx'
import SeloStatus from '../components/SeloStatus.jsx'
import { useAuth } from '../hooks/useAuth.jsx'
import { useDados } from '../hooks/useDados.js'
import { api } from '../services/api.js'
import { STATUS, formatarData } from '../utils/formato.js'

export default function Pedidos() {
  const { ehCliente, ehAgente } = useAuth()
  const carregar = useCallback(() => api.listarPedidos(), [])
  const { dados, erro, carregando } = useDados(carregar)
  const [filtro, setFiltro] = useState('TODOS')
  const pedidos = dados ?? []
  const visiveis = filtro === 'TODOS' ? pedidos : pedidos.filter((p) => p.status === filtro)

  return (
    <>
      <div className="cabecalho-pagina">
        <h1>{ehCliente ? 'Meus pedidos' : 'Pedidos de aluguel'}</h1>
        {ehCliente && <Link to="/pedidos/novo" className="botao botao-primario">Novo pedido</Link>}
      </div>
      <Aviso>{erro}</Aviso>
      {ehAgente && pedidos.length > 0 && (
        <div className="filtros" role="group" aria-label="Filtrar por situação">
          <button type="button" className={filtro === 'TODOS' ? 'ativo' : ''} onClick={() => setFiltro('TODOS')}>Todos</button>
          {Object.entries(STATUS).map(([chave, { rotulo }]) => (
            <button key={chave} type="button" className={filtro === chave ? 'ativo' : ''} onClick={() => setFiltro(chave)}>{rotulo}</button>
          ))}
        </div>
      )}
      {!carregando && !erro && visiveis.length === 0 && (
        <p className="vazio">
          {ehCliente
            ? <>Você ainda não fez pedidos. <Link to="/pedidos/novo">Faça o primeiro pedido</Link>.</>
            : 'Nenhum pedido nesta situação.'}
        </p>
      )}
      <ul className="lista-pedidos">
        {visiveis.map((pedido) => (
          <li key={pedido.id}>
            <Link to={`/pedidos/${pedido.id}`} className="linha-pedido">
              <Placa codigo={pedido.automovel.placa} tamanho="pequena" />
              <span className="linha-carro">
                <strong>{pedido.automovel.marca} {pedido.automovel.modelo}</strong>
                {ehAgente && <span>{pedido.cliente.nome}</span>}
              </span>
              <span className="linha-periodo">{formatarData(pedido.dataInicio)} a {formatarData(pedido.dataFim)}</span>
              <SeloStatus status={pedido.status} />
            </Link>
          </li>
        ))}
      </ul>
    </>
  )
}
