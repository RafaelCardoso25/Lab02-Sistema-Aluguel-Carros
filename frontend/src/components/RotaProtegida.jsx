import { Navigate, Outlet, useLocation } from 'react-router-dom'
import { useAuth } from '../hooks/useAuth.jsx'

export default function RotaProtegida({ perfis }) {
  const { sessao, carregando } = useAuth()
  const local = useLocation()

  if (carregando) return <p className="carregando">Carregando…</p>
  if (!sessao) return <Navigate to="/login" replace state={{ de: local.pathname }} />
  if (perfis && !perfis.includes(sessao.perfil)) return <Navigate to="/pedidos" replace />
  return <Outlet />
}
