import { useCallback, useState } from 'react'
import { api } from '../services/api.js'
import { useDados } from '../hooks/useDados.js'
import { hojeIso } from '../utils/formato.js'
import Aviso from './Aviso.jsx'
import Campo from './Campo.jsx'
import Placa from './Placa.jsx'

export default function FormularioPedido({ inicial, rotuloEnviar, onEnviar, onCancelar }) {
  const carregar = useCallback(() => api.listarAutomoveis(), [])
  const { dados: automoveis, erro: erroLista, carregando } = useDados(carregar)
  const [form, setForm] = useState({
    automovelId: inicial?.automovelId ?? '',
    dataInicio: inicial?.dataInicio ?? '',
    dataFim: inicial?.dataFim ?? '',
  })
  const [erro, setErro] = useState(null)
  const [campos, setCampos] = useState({})
  const [enviando, setEnviando] = useState(false)

  const disponiveis = (automoveis ?? []).filter((a) => a.disponivel)
  const escolhido = disponiveis.find((a) => String(a.id) === String(form.automovelId))
  const alterar = (campo) => (e) => setForm({ ...form, [campo]: e.target.value })

  const enviar = async (e) => {
    e.preventDefault()
    setEnviando(true)
    setErro(null)
    setCampos({})
    try {
      await onEnviar({ ...form, automovelId: Number(form.automovelId) })
    } catch (falha) {
      setErro(falha.message)
      setCampos(falha.campos ?? {})
      setEnviando(false)
    }
  }

  if (!carregando && !erroLista && disponiveis.length === 0) {
    return <p className="vazio">Nenhum automóvel disponível agora. Os carros aparecem aqui quando uma empresa ou um banco os cadastra.</p>
  }

  return (
    <form className="formulario" onSubmit={enviar}>
      <Aviso>{erroLista ?? erro}</Aviso>
      <Campo rotulo="Automóvel" value={form.automovelId} onChange={alterar('automovelId')} erro={campos.automovelId} required>
        <option value="">Escolha um automóvel</option>
        {disponiveis.map((a) => (
          <option key={a.id} value={a.id}>{a.marca} {a.modelo} {a.ano} · {a.placa}</option>
        ))}
      </Campo>
      {escolhido && (
        <div className="previa-carro">
          <Placa codigo={escolhido.placa} />
          <span>{escolhido.marca} {escolhido.modelo}, {escolhido.ano}. Registrado por {escolhido.proprietarioNome}.</span>
        </div>
      )}
      <div className="colunas">
        <Campo rotulo="Retirada" type="date" min={hojeIso()} value={form.dataInicio} onChange={alterar('dataInicio')} erro={campos.dataInicio} required />
        <Campo rotulo="Devolução" type="date" min={form.dataInicio || hojeIso()} value={form.dataFim} onChange={alterar('dataFim')} erro={campos.dataFim} required />
      </div>
      <div className="acoes">
        <button type="submit" className="botao botao-primario" disabled={enviando}>{rotuloEnviar}</button>
        {onCancelar && <button type="button" className="botao botao-discreto" onClick={onCancelar}>Voltar</button>}
      </div>
    </form>
  )
}
