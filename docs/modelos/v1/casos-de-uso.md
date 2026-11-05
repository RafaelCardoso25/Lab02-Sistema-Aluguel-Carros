# Diagrama de Casos de Uso

```mermaid
flowchart LR
    Usuario(["👤 Usuário"])
    Cliente(["👤 Cliente"])
    Agente(["👤 Agente"])
    Empresa(["👤 Empresa"])
    Banco(["👤 Banco"])

    Cliente -. generaliza .-> Usuario
    Agente -. generaliza .-> Usuario
    Empresa -. generaliza .-> Agente
    Banco -. generaliza .-> Agente

    subgraph Sistema["Sistema de Aluguel de Carros"]
        UC01(["UC01 Realizar login"])
        UC02(["UC02 Cadastrar-se"])
        UC03(["UC03 Manter dados do cliente"])
        UC04(["UC04 Introduzir pedido"])
        UC05(["UC05 Consultar pedido"])
        UC06(["UC06 Modificar pedido"])
        UC07(["UC07 Cancelar pedido"])
        UC08(["UC08 Avaliar pedido"])
        UC09(["UC09 Executar contrato"])
        UC10(["UC10 Conceder crédito"])
        UC11(["UC11 Manter automóveis"])
        UC12(["UC12 Consultar automóveis"])
        UC13(["UC13 Consultar contratos"])
        UC14(["UC14 Consultar clientes"])
    end

    Usuario --- UC01
    Usuario --- UC02
    Usuario --- UC05
    Usuario --- UC06
    Usuario --- UC12
    Usuario --- UC13
    Cliente --- UC03
    Cliente --- UC04
    Cliente --- UC07
    Agente --- UC08
    Agente --- UC09
    Agente --- UC11
    Agente --- UC14
    Banco --- UC10
    UC10 -. extend .-> UC09
```
