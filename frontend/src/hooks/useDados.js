import { useCallback, useEffect, useState } from 'react'

export function useDados(carregar) {
  const [dados, setDados] = useState(null)
  const [erro, setErro] = useState(null)
  const [carregando, setCarregando] = useState(true)

  const recarregar = useCallback(async () => {
    setCarregando(true)
    try {
      setDados(await carregar())
      setErro(null)
    } catch (e) {
      setErro(e.message)
    } finally {
      setCarregando(false)
    }
  }, [carregar])

  useEffect(() => {
    recarregar()
  }, [recarregar])

  return { dados, setDados, erro, carregando, recarregar }
}
