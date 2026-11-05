import { Navigate, Route, Routes } from 'react-router-dom'
import Layout from './components/Layout.jsx'
import RotaProtegida from './components/RotaProtegida.jsx'
import Automoveis from './pages/Automoveis.jsx'
import Cadastro from './pages/Cadastro.jsx'
import Clientes from './pages/Clientes.jsx'
import Contratos from './pages/Contratos.jsx'
import DetalhePedido from './pages/DetalhePedido.jsx'
import Login from './pages/Login.jsx'
import NovoPedido from './pages/NovoPedido.jsx'
import Pedidos from './pages/Pedidos.jsx'
import Perfil from './pages/Perfil.jsx'

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/cadastro" element={<Cadastro />} />
      <Route element={<RotaProtegida />}>
        <Route element={<Layout />}>
          <Route index element={<Navigate to="/pedidos" replace />} />
          <Route path="/pedidos" element={<Pedidos />} />
          <Route path="/pedidos/:id" element={<DetalhePedido />} />
          <Route path="/automoveis" element={<Automoveis />} />
          <Route path="/contratos" element={<Contratos />} />
          <Route element={<RotaProtegida perfis={['CLIENTE']} />}>
            <Route path="/pedidos/novo" element={<NovoPedido />} />
            <Route path="/perfil" element={<Perfil />} />
          </Route>
          <Route element={<RotaProtegida perfis={['EMPRESA', 'BANCO']} />}>
            <Route path="/clientes" element={<Clientes />} />
          </Route>
        </Route>
      </Route>
      <Route path="*" element={<Navigate to="/pedidos" replace />} />
    </Routes>
  )
}
