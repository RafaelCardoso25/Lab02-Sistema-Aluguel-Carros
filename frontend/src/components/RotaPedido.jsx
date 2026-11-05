const PARADAS = [
  { chave: 'ENVIADO', rotulo: 'Enviado' },
  { chave: 'EM_ANALISE', rotulo: 'Análise financeira' },
  { chave: 'APROVADO', rotulo: 'Parecer positivo' },
  { chave: 'CONTRATADO', rotulo: 'Contrato' },
]

const ALCANCE = { EM_ANALISE: 1, APROVADO: 2, CONTRATADO: 3 }

const FIM_DE_LINHA = {
  REPROVADO: { indice: 2, rotulo: 'Reprovado' },
  CANCELADO: { indice: null, rotulo: 'Cancelado' },
}

export default function RotaPedido({ status, avaliado = false }) {
  const fim = FIM_DE_LINHA[status]
  const indiceFim = fim ? (fim.indice ?? (avaliado ? 3 : 2)) : null
  const atual = fim ? indiceFim : ALCANCE[status]

  return (
    <ol className="rota" aria-label="Andamento do pedido">
      {PARADAS.map((parada, indice) => {
        const interrompida = indiceFim === indice
        const estado = interrompida
          ? 'interrompida'
          : indice < atual
            ? 'percorrida'
            : indice === atual
              ? 'atual'
              : 'futura'
        return (
          <li
            key={parada.chave}
            className={`parada parada-${estado}`}
            aria-current={indice === atual ? 'step' : undefined}
          >
            <span className="parada-marco" aria-hidden="true" />
            <span className="parada-rotulo">{interrompida ? fim.rotulo : parada.rotulo}</span>
          </li>
        )
      })}
    </ol>
  )
}
