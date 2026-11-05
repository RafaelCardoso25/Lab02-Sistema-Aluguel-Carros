import { useCallback, useState } from 'react'
import { Link } from 'react-router-dom'
import Aviso from '../components/Aviso.jsx'
import Campo from '../components/Campo.jsx'
import Placa from '../components/Placa.jsx'
import { useAuth } from '../hooks/useAuth.jsx'
import { useDados } from '../hooks/useDados.js'
import { api } from '../services/api.js'
import { PERFIS } from '../utils/formato.js'

const VAZIO = { matricula: '', ano: '', marca: '', modelo: '', placa: '' }

function FormularioAutomovel({ inicial, onSalvar, onFechar }) {
  const [form, setForm] = useState(inicial ?? VAZIO)
  const [erro, setErro] = useState(null)
  const [campos, setCampos] = useState({})
  const [enviando, setEnviando] = useState(false)
  const alterar = (campo) => (e) => setForm({ ...form, [campo]: e.target.value })

  const enviar = async (e) => {
    e.preventDefault()
    setEnviando(true)
    setErro(null)
    setCampos({})
    try {
      await onSalvar({ ...form, ano: Number(form.ano) })
    } catch (falha) {
      setErro(falha.message)
      setCampos(falha.campos ?? {})
      setEnviando(false)
    }
  }

  return (
    <form className="formulario cartao" onSubmit={enviar}>
      <h2>{inicial ? 'Editar automóvel' : 'Cadastrar automóvel'}</h2>
      <Aviso>{erro}</Aviso>
      <div className="colunas">
        <Campo rotulo="Marca" value={form.marca} onChange={alterar('marca')} erro={campos.marca} required />
        <Campo rotulo="Modelo" value={form.modelo} onChange={alterar('modelo')} erro={campos.modelo} required />
      </div>
      <div className="colunas colunas-3">
        <Campo rotulo="Ano" type="number" min="1950" max="2100" value={form.ano} onChange={alterar('ano')} erro={campos.ano} required />
        <Campo rotulo="Placa" value={form.placa} onChange={(e) => setForm({ ...form, placa: e.target.value.toUpperCase() })} erro={campos.placa} placeholder="ABC1D23" required />
        <Campo rotulo="Matrícula" value={form.matricula} onChange={alterar('matricula')} erro={campos.matricula} required />
      </div>
      <div className="acoes">
        <button type="submit" className="botao botao-primario" disabled={enviando}>{inicial ? 'Salvar alterações' : 'Cadastrar automóvel'}</button>
        <button type="button" className="botao botao-discreto" onClick={onFechar}>Fechar</button>
      </div>
    </form>
  )
}

export default function Automoveis() {
  const { sessao, ehAgente, ehCliente } = useAuth()
  const carregar = useCallback(() => api.listarAutomoveis(), [])
  const { dados, erro, carregando, recarregar } = useDados(carregar)
  const [editor, setEditor] = useState(null)
  const [erroAcao, setErroAcao] = useState(null)
  const automoveis = dados ?? []

  const salvar = async (form) => {
    if (editor === 'novo') {
      await api.criarAutomovel(form)
    } else {
      await api.atualizarAutomovel(editor.id, form)
    }
    setEditor(null)
    await recarregar()
  }

  const excluir = async (automovel) => {
    if (!window.confirm(`Excluir o automóvel de placa ${automovel.placa}?`)) return
    setErroAcao(null)
    try {
      await api.excluirAutomovel(automovel.id)
      await recarregar()
    } catch (falha) {
      setErroAcao(falha.message)
    }
  }

  return (
    <>
      <div className="cabecalho-pagina">
        <h1>Automóveis</h1>
        {ehAgente && editor === null && (
          <button type="button" className="botao botao-primario" onClick={() => setEditor('novo')}>Cadastrar automóvel</button>
        )}
      </div>
      <Aviso>{erro ?? erroAcao}</Aviso>
      {editor !== null && (
        <FormularioAutomovel
          key={editor === 'novo' ? 'novo' : editor.id}
          inicial={editor === 'novo' ? null : editor}
          onSalvar={salvar}
          onFechar={() => setEditor(null)}
        />
      )}
      {!carregando && !erro && automoveis.length === 0 && (
        <p className="vazio">{ehAgente ? 'Nenhum automóvel cadastrado. Cadastre o primeiro para receber pedidos.' : 'Nenhum automóvel cadastrado ainda. Os carros aparecem aqui quando uma empresa ou um banco os cadastra.'}</p>
      )}
      <ul className="grade-carros">
        {automoveis.map((automovel) => (
          <li key={automovel.id} className="cartao cartao-carro">
            <Placa codigo={automovel.placa} />
            <h2>{automovel.marca} {automovel.modelo}</h2>
            <dl className="dados">
              <dt>Ano</dt><dd>{automovel.ano}</dd>
              <dt>Matrícula</dt><dd>{automovel.matricula}</dd>
              <dt>Proprietário</dt><dd>{automovel.proprietarioNome} ({PERFIS[automovel.proprietarioPerfil]})</dd>
            </dl>
            <div className="acoes">
              {ehCliente && automovel.disponivel && (
                <Link to={`/pedidos/novo?automovel=${automovel.id}`} className="botao botao-secundario">Pedir este carro</Link>
              )}
              {ehAgente && automovel.proprietarioId === sessao.id && (
                <>
                  <button type="button" className="botao botao-secundario" onClick={() => setEditor(automovel)}>Editar</button>
                  <button type="button" className="botao botao-perigo" onClick={() => excluir(automovel)}>Excluir</button>
                </>
              )}
            </div>
          </li>
        ))}
      </ul>
    </>
  )
}
