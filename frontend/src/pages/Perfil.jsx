import { useCallback, useEffect, useState } from 'react'
import Aviso from '../components/Aviso.jsx'
import Campo from '../components/Campo.jsx'
import EditorRendimentos from '../components/EditorRendimentos.jsx'
import { useAuth } from '../hooks/useAuth.jsx'
import { useDados } from '../hooks/useDados.js'
import { api } from '../services/api.js'

export default function Perfil() {
  const { sessao, sair } = useAuth()
  const carregar = useCallback(() => api.buscarCliente(sessao.id), [sessao.id])
  const { dados: cliente, erro: erroCarga } = useDados(carregar)
  const [form, setForm] = useState(null)
  const [novaSenha, setNovaSenha] = useState('')
  const [erro, setErro] = useState(null)
  const [campos, setCampos] = useState({})
  const [salvo, setSalvo] = useState(false)
  const [enviando, setEnviando] = useState(false)

  useEffect(() => {
    if (cliente) setForm(cliente)
  }, [cliente])

  if (erroCarga) return <Aviso>{erroCarga}</Aviso>
  if (!form) return <p className="carregando">Carregando…</p>

  const alterar = (campo) => (e) => {
    setSalvo(false)
    setForm({ ...form, [campo]: e.target.value })
  }

  const salvar = async (e) => {
    e.preventDefault()
    setEnviando(true)
    setErro(null)
    setCampos({})
    setSalvo(false)
    try {
      const { nome, rg, cpf, endereco, profissao, rendimentos } = form
      const atualizado = await api.atualizarCliente(sessao.id, {
        nome,
        rg,
        cpf,
        endereco,
        profissao,
        rendimentos,
        novaSenha: novaSenha || null,
      })
      setForm(atualizado)
      setNovaSenha('')
      setSalvo(true)
    } catch (falha) {
      setErro(falha.message)
      setCampos(falha.campos ?? {})
    } finally {
      setEnviando(false)
    }
  }

  const excluir = async () => {
    if (!window.confirm('Excluir seu cadastro? Essa ação não pode ser desfeita.')) return
    setErro(null)
    try {
      await api.excluirCliente(sessao.id)
      sair()
    } catch (falha) {
      setErro(falha.message)
    }
  }

  return (
    <>
      <h1>Meus dados</h1>
      <form className="formulario cartao" onSubmit={salvar}>
        <Aviso>{erro}</Aviso>
        {salvo && <Aviso tipo="sucesso">Dados salvos.</Aviso>}
        <Campo rotulo="Nome completo" value={form.nome} onChange={alterar('nome')} erro={campos.nome} required />
        <div className="colunas">
          <Campo rotulo="RG" value={form.rg} onChange={alterar('rg')} erro={campos.rg} required />
          <Campo rotulo="CPF" value={form.cpf} onChange={alterar('cpf')} erro={campos.cpf} required />
        </div>
        <Campo rotulo="Endereço" value={form.endereco} onChange={alterar('endereco')} erro={campos.endereco} required />
        <Campo rotulo="Profissão" value={form.profissao} onChange={alterar('profissao')} erro={campos.profissao} required />
        <EditorRendimentos
          rendimentos={form.rendimentos}
          onChange={(rendimentos) => {
            setSalvo(false)
            setForm({ ...form, rendimentos })
          }}
          erros={campos}
        />
        <Campo
          rotulo="Nova senha"
          type="password"
          value={novaSenha}
          onChange={(e) => setNovaSenha(e.target.value)}
          erro={campos.novaSenha}
          ajuda="Deixe em branco para manter a senha atual."
          autoComplete="new-password"
        />
        <div className="acoes">
          <button type="submit" className="botao botao-primario" disabled={enviando}>Salvar alterações</button>
          <button type="button" className="botao botao-perigo" onClick={excluir}>Excluir cadastro</button>
        </div>
      </form>
    </>
  )
}
