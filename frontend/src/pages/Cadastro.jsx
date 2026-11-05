import { useState } from 'react'
import { Link, Navigate, useNavigate } from 'react-router-dom'
import Aviso from '../components/Aviso.jsx'
import Campo from '../components/Campo.jsx'
import EditorRendimentos from '../components/EditorRendimentos.jsx'
import { useAuth } from '../hooks/useAuth.jsx'
import { api } from '../services/api.js'
import { PERFIS } from '../utils/formato.js'

const VAZIO = {
  login: '',
  senha: '',
  nome: '',
  rg: '',
  cpf: '',
  endereco: '',
  profissao: '',
  cnpj: '',
}

export default function Cadastro() {
  const { sessao, entrar } = useAuth()
  const navegar = useNavigate()
  const [perfil, setPerfil] = useState('CLIENTE')
  const [form, setForm] = useState(VAZIO)
  const [rendimentos, setRendimentos] = useState([])
  const [erro, setErro] = useState(null)
  const [campos, setCampos] = useState({})
  const [enviando, setEnviando] = useState(false)
  const ehCliente = perfil === 'CLIENTE'

  if (sessao && !enviando) return <Navigate to="/pedidos" replace />

  const alterar = (campo) => (e) => setForm({ ...form, [campo]: e.target.value })

  const enviar = async (e) => {
    e.preventDefault()
    setEnviando(true)
    setErro(null)
    setCampos({})
    try {
      if (ehCliente) {
        const { login, senha, nome, rg, cpf, endereco, profissao } = form
        await api.cadastrarCliente({ login, senha, nome, rg, cpf, endereco, profissao, rendimentos })
      } else {
        const { login, senha, nome, cnpj } = form
        await api.cadastrarAgente({ login, senha, nome, cnpj, tipo: perfil })
      }
      await entrar(form.login, form.senha)
      navegar('/pedidos', { replace: true })
    } catch (falha) {
      setErro(falha.message)
      setCampos(falha.campos ?? {})
      setEnviando(false)
    }
  }

  return (
    <div className="pagina-cadastro">
      <h1>Criar cadastro</h1>
      <p className="subtitulo">Escolha como você vai usar o sistema.</p>
      <div className="seletor" role="radiogroup" aria-label="Tipo de cadastro">
        {Object.entries(PERFIS).map(([chave, rotulo]) => (
          <button
            key={chave}
            type="button"
            role="radio"
            aria-checked={perfil === chave}
            className={perfil === chave ? 'ativo' : ''}
            onClick={() => setPerfil(chave)}
          >
            {rotulo}
          </button>
        ))}
      </div>
      <form className="formulario cartao" onSubmit={enviar}>
        <Aviso>{erro}</Aviso>
        <Campo rotulo={ehCliente ? 'Nome completo' : 'Razão social'} value={form.nome} onChange={alterar('nome')} erro={campos.nome} required />
        {ehCliente ? (
          <>
            <div className="colunas">
              <Campo rotulo="RG" value={form.rg} onChange={alterar('rg')} erro={campos.rg} required />
              <Campo rotulo="CPF" value={form.cpf} onChange={alterar('cpf')} erro={campos.cpf} inputMode="numeric" required />
            </div>
            <Campo rotulo="Endereço" value={form.endereco} onChange={alterar('endereco')} erro={campos.endereco} required />
            <Campo rotulo="Profissão" value={form.profissao} onChange={alterar('profissao')} erro={campos.profissao} required />
            <EditorRendimentos rendimentos={rendimentos} onChange={setRendimentos} erros={campos} />
          </>
        ) : (
          <Campo rotulo="CNPJ" value={form.cnpj} onChange={alterar('cnpj')} erro={campos.cnpj} inputMode="numeric" required />
        )}
        <div className="colunas">
          <Campo rotulo="Usuário" value={form.login} onChange={alterar('login')} erro={campos.login} autoComplete="username" required />
          <Campo rotulo="Senha" type="password" value={form.senha} onChange={alterar('senha')} erro={campos.senha} ajuda="Use pelo menos 8 caracteres." autoComplete="new-password" required />
        </div>
        <button type="submit" className="botao botao-primario botao-largo" disabled={enviando}>Criar cadastro</button>
      </form>
      <p className="entrada-rodape">Já tem cadastro? <Link to="/login">Entrar</Link></p>
    </div>
  )
}
