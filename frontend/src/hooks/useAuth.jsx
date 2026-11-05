import { createContext, useCallback, useContext, useEffect, useMemo, useState } from 'react'
import { api, lerToken, salvarToken } from '../services/api.js'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [sessao, setSessao] = useState(null)
  const [carregando, setCarregando] = useState(Boolean(lerToken()))

  useEffect(() => {
    if (!lerToken()) return
    api.sessao()
      .then(setSessao)
      .catch(() => {
        salvarToken(null)
        setSessao(null)
      })
      .finally(() => setCarregando(false))
  }, [])

  useEffect(() => {
    const aoExpirar = () => setSessao(null)
    window.addEventListener('sessao-expirada', aoExpirar)
    return () => window.removeEventListener('sessao-expirada', aoExpirar)
  }, [])

  const entrar = useCallback(async (login, senha) => {
    const nova = await api.entrar({ login, senha })
    salvarToken(nova.token)
    setSessao(nova)
    return nova
  }, [])

  const sair = useCallback(() => {
    salvarToken(null)
    setSessao(null)
  }, [])

  const valor = useMemo(() => ({
    sessao,
    carregando,
    entrar,
    sair,
    ehCliente: sessao?.perfil === 'CLIENTE',
    ehAgente: sessao?.perfil === 'EMPRESA' || sessao?.perfil === 'BANCO',
  }), [sessao, carregando, entrar, sair])

  return <AuthContext.Provider value={valor}>{children}</AuthContext.Provider>
}

export function useAuth() {
  return useContext(AuthContext)
}
