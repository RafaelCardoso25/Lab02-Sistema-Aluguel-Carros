import { useCallback } from 'react'
import Aviso from '../components/Aviso.jsx'
import { useDados } from '../hooks/useDados.js'
import { api } from '../services/api.js'
import { formatarCpf, formatarMoeda } from '../utils/formato.js'

export default function Clientes() {
  const carregar = useCallback(() => api.listarClientes(), [])
  const { dados, erro, carregando } = useDados(carregar)
  const clientes = dados ?? []

  return (
    <>
      <h1>Clientes</h1>
      <Aviso>{erro}</Aviso>
      {!carregando && !erro && clientes.length === 0 && <p className="vazio">Nenhum cliente cadastrado ainda.</p>}
      {clientes.length > 0 && (
        <div className="tabela-rolagem">
          <table className="tabela">
            <thead>
              <tr>
                <th scope="col">Nome</th>
                <th scope="col">CPF</th>
                <th scope="col">Profissão</th>
                <th scope="col">Entidades empregadoras</th>
                <th scope="col" className="numero">Renda mensal</th>
              </tr>
            </thead>
            <tbody>
              {clientes.map((cliente) => (
                <tr key={cliente.id}>
                  <td>{cliente.nome}</td>
                  <td>{formatarCpf(cliente.cpf)}</td>
                  <td>{cliente.profissao}</td>
                  <td>{cliente.rendimentos.map((r) => r.entidadeEmpregadora).join(', ') || 'Nenhuma'}</td>
                  <td className="numero">{formatarMoeda(cliente.rendaTotal)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
    </>
  )
}
