import { useState } from 'react'
import { Link, Navigate, useLocation, useNavigate } from 'react-router-dom'
import Aviso from '../components/Aviso.jsx'
import Campo from '../components/Campo.jsx'
import Placa from '../components/Placa.jsx'
import { useAuth } from '../hooks/useAuth.jsx'

export default function Login() {
  const { sessao, entrar } = useAuth()
  const navegar = useNavigate()
  const local = useLocation()
  const [login, setLogin] = useState('')
  const [senha, setSenha] = useState('')
  const [erro, setErro] = useState(null)
  const [enviando, setEnviando] = useState(false)

  if (sessao) return <Navigate to="/pedidos" replace />

  const enviar = async (e) => {
    e.preventDefault()
    setEnviando(true)
    setErro(null)
    try {
      await entrar(login, senha)
      navegar(local.state?.de ?? '/pedidos', { replace: true })
    } catch (falha) {
      setErro(falha.message)
      setEnviando(false)
    }
  }

  return (
    <div className="entrada">
      <section className="entrada-estrada" aria-hidden="true">
        <Placa codigo="R0D4G3M" tamanho="gigante" />
        <div className="estrada-faixa" />
      </section>
      <section className="entrada-painel">
        <h1>Peça seu carro pela internet</h1>
        <p className="entrada-texto">Clientes pedem e acompanham o aluguel. Empresas e bancos analisam e fecham o contrato.</p>
        <form className="formulario" onSubmit={enviar}>
          <Aviso>{erro}</Aviso>
          <Campo rotulo="Usuário" value={login} onChange={(e) => setLogin(e.target.value)} autoComplete="username" required />
          <Campo rotulo="Senha" type="password" value={senha} onChange={(e) => setSenha(e.target.value)} autoComplete="current-password" required />
          <button type="submit" className="botao botao-primario botao-largo" disabled={enviando}>Entrar</button>
        </form>
        <p className="entrada-rodape">Ainda não tem cadastro? <Link to="/cadastro">Criar cadastro</Link></p>
      </section>
    </div>
  )
}
