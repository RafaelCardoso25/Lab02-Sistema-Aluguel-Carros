import { useId } from 'react'

export default function Campo({ rotulo, erro, ajuda, children, ...props }) {
  const id = useId()
  const descricao = erro ? `${id}-erro` : ajuda ? `${id}-ajuda` : undefined
  const comum = { id, 'aria-invalid': Boolean(erro), 'aria-describedby': descricao, ...props }

  return (
    <div className={`campo${erro ? ' campo-erro' : ''}`}>
      <label htmlFor={id}>{rotulo}</label>
      {children ? <select {...comum}>{children}</select> : <input {...comum} />}
      {ajuda && !erro && <small id={`${id}-ajuda`}>{ajuda}</small>}
      {erro && <small id={`${id}-erro`} className="mensagem-campo">{erro}</small>}
    </div>
  )
}
