const BASE_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080/api'
const CHAVE_TOKEN = 'rodagem.token'

export class ErroApi extends Error {
  constructor(mensagem, status, campos = {}) {
    super(mensagem)
    this.status = status
    this.campos = campos
  }
}

export function lerToken() {
  try {
    return localStorage.getItem(CHAVE_TOKEN)
  } catch {
    return null
  }
}

export function salvarToken(token) {
  try {
    if (token) {
      localStorage.setItem(CHAVE_TOKEN, token)
    } else {
      localStorage.removeItem(CHAVE_TOKEN)
    }
  } catch {
    return
  }
}

async function requisitar(caminho, { method = 'GET', body } = {}) {
  const token = lerToken()
  const headers = {}
  if (body !== undefined) headers['Content-Type'] = 'application/json'
  if (token) headers.Authorization = `Bearer ${token}`

  let resposta
  try {
    resposta = await fetch(`${BASE_URL}${caminho}`, {
      method,
      headers,
      body: body === undefined ? undefined : JSON.stringify(body),
    })
  } catch {
    throw new ErroApi('Não foi possível conectar ao servidor. Verifique se o back-end está rodando.', 0)
  }

  if (resposta.status === 401 && token) {
    salvarToken(null)
    window.dispatchEvent(new Event('sessao-expirada'))
    throw new ErroApi('Sua sessão expirou. Entre novamente.', 401)
  }

  const texto = await resposta.text()
  const dados = texto ? JSON.parse(texto) : null

  if (!resposta.ok) {
    throw new ErroApi(dados?.mensagem ?? 'Não foi possível concluir a ação.', resposta.status, dados?.campos ?? {})
  }
  return dados
}

export const api = {
  entrar: (credenciais) => requisitar('/auth/login', { method: 'POST', body: credenciais }),
  sessao: () => requisitar('/auth/me'),
  cadastrarCliente: (dados) => requisitar('/clientes', { method: 'POST', body: dados }),
  cadastrarAgente: (dados) => requisitar('/agentes', { method: 'POST', body: dados }),
  listarClientes: () => requisitar('/clientes'),
  buscarCliente: (id) => requisitar(`/clientes/${id}`),
  atualizarCliente: (id, dados) => requisitar(`/clientes/${id}`, { method: 'PUT', body: dados }),
  excluirCliente: (id) => requisitar(`/clientes/${id}`, { method: 'DELETE' }),
  listarAutomoveis: () => requisitar('/automoveis'),
  criarAutomovel: (dados) => requisitar('/automoveis', { method: 'POST', body: dados }),
  atualizarAutomovel: (id, dados) => requisitar(`/automoveis/${id}`, { method: 'PUT', body: dados }),
  excluirAutomovel: (id) => requisitar(`/automoveis/${id}`, { method: 'DELETE' }),
  listarPedidos: () => requisitar('/pedidos'),
  buscarPedido: (id) => requisitar(`/pedidos/${id}`),
  criarPedido: (dados) => requisitar('/pedidos', { method: 'POST', body: dados }),
  modificarPedido: (id, dados) => requisitar(`/pedidos/${id}`, { method: 'PUT', body: dados }),
  cancelarPedido: (id) => requisitar(`/pedidos/${id}/cancelamento`, { method: 'POST' }),
  avaliarPedido: (id, dados) => requisitar(`/pedidos/${id}/avaliacao`, { method: 'POST', body: dados }),
  listarContratos: () => requisitar('/contratos'),
  executarContrato: (dados) => requisitar('/contratos', { method: 'POST', body: dados }),
}
