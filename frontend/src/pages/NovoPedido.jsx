import { useNavigate, useSearchParams } from 'react-router-dom'
import FormularioPedido from '../components/FormularioPedido.jsx'
import { api } from '../services/api.js'

export default function NovoPedido() {
  const navegar = useNavigate()
  const [parametros] = useSearchParams()

  const criar = async (dados) => {
    const pedido = await api.criarPedido(dados)
    navegar(`/pedidos/${pedido.id}`, { replace: true })
  }

  return (
    <>
      <h1>Novo pedido</h1>
      <p className="subtitulo">Depois de enviado, seu pedido passa pela análise financeira de um agente.</p>
      <div className="cartao">
        <FormularioPedido
          inicial={{ automovelId: parametros.get('automovel') ?? '' }}
          rotuloEnviar="Enviar pedido"
          onEnviar={criar}
          onCancelar={() => navegar('/pedidos')}
        />
      </div>
    </>
  )
}
