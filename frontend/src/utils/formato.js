const moeda = new Intl.NumberFormat('pt-BR', { style: 'currency', currency: 'BRL' })

export const formatarMoeda = (valor) => moeda.format(Number(valor ?? 0))

export function formatarData(data) {
  if (!data) return ''
  const [ano, mes, dia] = data.split('-')
  return `${dia}/${mes}/${ano}`
}

export function formatarCpf(cpf) {
  return cpf.replace(/^(\d{3})(\d{3})(\d{3})(\d{2})$/, '$1.$2.$3-$4')
}

export function hojeIso() {
  const agora = new Date()
  const local = new Date(agora.getTime() - agora.getTimezoneOffset() * 60000)
  return local.toISOString().slice(0, 10)
}

export const STATUS = {
  EM_ANALISE: { rotulo: 'Em análise', classe: 'selo-analise' },
  APROVADO: { rotulo: 'Aprovado', classe: 'selo-aprovado' },
  REPROVADO: { rotulo: 'Reprovado', classe: 'selo-negado' },
  CANCELADO: { rotulo: 'Cancelado', classe: 'selo-negado' },
  CONTRATADO: { rotulo: 'Contratado', classe: 'selo-contratado' },
}

export const TIPOS_CONTRATO = {
  ALUGUEL_SIMPLES: {
    rotulo: 'Aluguel simples',
    descricao: 'O carro fica registrado no nome da sua empresa.',
    perfis: ['EMPRESA'],
  },
  ALUGUEL_COM_CREDITO: {
    rotulo: 'Aluguel com crédito',
    descricao: 'O carro fica registrado no nome do seu banco, que concede o crédito.',
    perfis: ['BANCO'],
  },
  ALUGUEL_COM_COMPRA: {
    rotulo: 'Aluguel com compra',
    descricao: 'O carro passa a ser registrado no nome do cliente.',
    perfis: ['EMPRESA', 'BANCO'],
  },
}

export const PERFIS = {
  CLIENTE: 'Cliente',
  EMPRESA: 'Empresa',
  BANCO: 'Banco',
}
