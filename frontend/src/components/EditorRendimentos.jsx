import Campo from './Campo.jsx'

const MAXIMO = 3

export default function EditorRendimentos({ rendimentos, onChange, erros = {} }) {
  const alterar = (indice, campo, valor) => {
    onChange(rendimentos.map((item, i) => (i === indice ? { ...item, [campo]: valor } : item)))
  }

  return (
    <fieldset className="grupo">
      <legend>Rendimentos</legend>
      <p className="grupo-ajuda">Informe até 3 entidades empregadoras e o rendimento mensal de cada uma.</p>
      {rendimentos.map((item, indice) => (
        <div className="linha-rendimento" key={indice}>
          <Campo
            rotulo="Entidade empregadora"
            value={item.entidadeEmpregadora}
            onChange={(e) => alterar(indice, 'entidadeEmpregadora', e.target.value)}
            erro={erros[`rendimentos[${indice}].entidadeEmpregadora`]}
            required
          />
          <Campo
            rotulo="Rendimento mensal (R$)"
            type="number"
            min="0.01"
            step="0.01"
            value={item.valor}
            onChange={(e) => alterar(indice, 'valor', e.target.value)}
            erro={erros[`rendimentos[${indice}].valor`]}
            required
          />
          <button
            type="button"
            className="botao botao-discreto"
            onClick={() => onChange(rendimentos.filter((_, i) => i !== indice))}
          >
            Remover
          </button>
        </div>
      ))}
      {erros.rendimentos && <small className="mensagem-campo">{erros.rendimentos}</small>}
      <button
        type="button"
        className="botao botao-secundario"
        disabled={rendimentos.length >= MAXIMO}
        onClick={() => onChange([...rendimentos, { entidadeEmpregadora: '', valor: '' }])}
      >
        Adicionar rendimento
      </button>
    </fieldset>
  )
}
