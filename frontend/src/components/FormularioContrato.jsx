import { useState } from 'react'
import { TIPOS_CONTRATO, formatarMoeda } from '../utils/formato.js'
import Aviso from './Aviso.jsx'
import Campo from './Campo.jsx'

export default function FormularioContrato({ perfil, onEnviar }) {
  const tipos = Object.entries(TIPOS_CONTRATO).filter(([, tipo]) => tipo.perfis.includes(perfil))
  const [tipo, setTipo] = useState(tipos[0][0])
  const [valor, setValor] = useState('')
  const [credito, setCredito] = useState({ valor: '', taxaJurosMensal: '', parcelas: '' })
  const [erro, setErro] = useState(null)
  const [campos, setCampos] = useState({})
  const [enviando, setEnviando] = useState(false)
  const comCredito = tipo === 'ALUGUEL_COM_CREDITO'

  const enviar = async (e) => {
    e.preventDefault()
    setEnviando(true)
    setErro(null)
    setCampos({})
    try {
      await onEnviar({
        tipo,
        valor,
        credito: comCredito ? { ...credito, parcelas: Number(credito.parcelas) } : null,
      })
    } catch (falha) {
      setErro(falha.message)
      setCampos(falha.campos ?? {})
      setEnviando(false)
    }
  }

  const parcela = comCredito && credito.valor && credito.parcelas
    ? Number(credito.valor) / Number(credito.parcelas)
    : null

  return (
    <form className="formulario" onSubmit={enviar}>
      <Aviso>{erro}</Aviso>
      <Campo
        rotulo="Tipo de contrato"
        value={tipo}
        onChange={(e) => setTipo(e.target.value)}
        ajuda={TIPOS_CONTRATO[tipo].descricao}
      >
        {tipos.map(([chave, item]) => <option key={chave} value={chave}>{item.rotulo}</option>)}
      </Campo>
      <Campo
        rotulo="Valor do contrato (R$)"
        type="number"
        min="0.01"
        step="0.01"
        value={valor}
        onChange={(e) => setValor(e.target.value)}
        erro={campos.valor}
        required
      />
      {comCredito && (
        <fieldset className="grupo">
          <legend>Contrato de crédito</legend>
          <div className="colunas colunas-3">
            <Campo
              rotulo="Valor do crédito (R$)"
              type="number"
              min="0.01"
              step="0.01"
              value={credito.valor}
              onChange={(e) => setCredito({ ...credito, valor: e.target.value })}
              erro={campos['credito.valor']}
              required
            />
            <Campo
              rotulo="Juros ao mês (%)"
              type="number"
              min="0"
              step="0.01"
              value={credito.taxaJurosMensal}
              onChange={(e) => setCredito({ ...credito, taxaJurosMensal: e.target.value })}
              erro={campos['credito.taxaJurosMensal']}
              required
            />
            <Campo
              rotulo="Parcelas"
              type="number"
              min="1"
              step="1"
              value={credito.parcelas}
              onChange={(e) => setCredito({ ...credito, parcelas: e.target.value })}
              erro={campos['credito.parcelas']}
              required
            />
          </div>
          {parcela !== null && <small>Sem juros, cada parcela ficaria em {formatarMoeda(parcela)}.</small>}
        </fieldset>
      )}
      <div className="acoes">
        <button type="submit" className="botao botao-primario" disabled={enviando}>Executar contrato</button>
      </div>
    </form>
  )
}
