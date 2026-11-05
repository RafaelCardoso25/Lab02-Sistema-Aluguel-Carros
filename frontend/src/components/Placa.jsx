export default function Placa({ codigo, tamanho = 'media' }) {
  return (
    <span className={`placa placa-${tamanho}`} aria-label={`Placa ${codigo}`}>
      <span className="placa-faixa" aria-hidden="true">
        <span>BRASIL</span>
      </span>
      <span className="placa-codigo">{codigo}</span>
    </span>
  )
}
