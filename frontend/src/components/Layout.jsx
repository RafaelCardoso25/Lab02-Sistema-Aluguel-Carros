import { NavLink, Outlet } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth.jsx'
import { PERFIS } from '../utils/formato.js'

export default function Layout() {
  const { sessao, sair, ehCliente, ehAgente } = useAuth()

  return (
    <>
      <header className="topo">
        <div className="topo-conteudo">
          <NavLink to="/pedidos" className="marca">Rodagem</NavLink>
          <nav className="navegacao" aria-label="Principal">
            <NavLink to="/pedidos">Pedidos</NavLink>
            <NavLink to="/automoveis">Automóveis</NavLink>
            <NavLink to="/contratos">Contratos</NavLink>
            {ehAgente && <NavLink to="/clientes">Clientes</NavLink>}
            {ehCliente && <NavLink to="/perfil">Meus dados</NavLink>}
          </nav>
          <div className="usuario">
            <span className="usuario-nome">{sessao.nome}</span>
            <span className="usuario-perfil">{PERFIS[sessao.perfil]}</span>
            <button type="button" className="botao-sair" onClick={sair}>Sair</button>
          </div>
        </div>
        <div className="faixa-central" aria-hidden="true" />
      </header>
      <main className="conteudo">
        <Outlet />
      </main>
    </>
  )
}
