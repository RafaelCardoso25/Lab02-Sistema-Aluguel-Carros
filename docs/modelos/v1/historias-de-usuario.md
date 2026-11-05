# Histórias de Usuário

## Autenticação e cadastro

| ID | História | Critérios de aceitação | Caso de uso |
| --- | --- | --- | --- |
| US01 | Como usuário, quero entrar com usuário e senha para acessar o sistema. | Credenciais corretas abrem o sistema. Credenciais incorretas mostram erro. Sem login, toda rota interna redireciona para a tela de login. | UC01 |
| US02 | Como pessoa física, quero me cadastrar como cliente para pedir aluguéis. | RG, CPF, nome, endereço, profissão, usuário e senha são obrigatórios. CPF inválido é recusado. CPF, RG e usuário não se repetem. | UC02 |
| US03 | Como empresa ou banco, quero me cadastrar como agente para avaliar pedidos. | Razão social, CNPJ, usuário e senha são obrigatórios. CNPJ inválido é recusado. CNPJ e usuário não se repetem. | UC02 |

## Cliente

| ID | História | Critérios de aceitação | Caso de uso |
| --- | --- | --- | --- |
| US04 | Como cliente, quero consultar e alterar meus dados para mantê-los atualizados. | O cliente só acessa o próprio cadastro. A senha só muda se uma nova for informada. | UC03 |
| US05 | Como cliente, quero informar minhas entidades empregadoras e rendimentos para embasar a análise financeira. | Aceita no máximo 3 rendimentos. O valor precisa ser maior que zero. | UC03 |
| US06 | Como cliente, quero excluir meu cadastro quando não usar mais o sistema. | Cliente sem pedidos é excluído. Cliente com pedidos registrados não pode ser excluído. | UC03 |
| US07 | Como cliente, quero introduzir um pedido de aluguel para reservar um automóvel. | Exige automóvel disponível, retirada e devolução. A retirada não pode estar no passado nem a devolução antes da retirada. Carro já contratado no período é recusado. O pedido nasce em análise. | UC04 |
| US08 | Como cliente, quero consultar meus pedidos para acompanhar o status. | A lista mostra só os pedidos do cliente. O detalhe mostra o andamento, o parecer e o contrato. | UC05 |
| US09 | Como cliente, quero modificar meu pedido enquanto estiver em análise. | Só é possível no status em análise. As regras de datas e disponibilidade continuam valendo. | UC06 |
| US10 | Como cliente, quero cancelar meu pedido se desistir do aluguel. | Só é possível em análise ou aprovado. Pedido contratado não pode ser cancelado. | UC07 |

## Agente

| ID | História | Critérios de aceitação | Caso de uso |
| --- | --- | --- | --- |
| US11 | Como agente, quero ver os pedidos e os dados financeiros do contratante para decidir com segurança. | A lista mostra todos os pedidos, com filtro por status. O detalhe mostra identificação, profissão, rendimentos e renda total. | UC05, UC14 |
| US12 | Como agente, quero modificar um pedido para ajustar as condições do aluguel. | Só é possível em análise ou aprovado. | UC06 |
| US13 | Como agente, quero avaliar um pedido do ponto de vista financeiro. | O parecer é obrigatório. Só pedidos em análise são avaliados. O resultado é aprovado ou reprovado. | UC08 |
| US14 | Como agente, quero executar o contrato de um pedido aprovado para formalizar o aluguel. | Só pedidos aprovados viram contrato. Pedido e contrato ficam em relação um para um. O pedido passa a contratado. | UC09 |
| US15 | Como agente, quero que o registro do automóvel siga o tipo de contrato. | Aluguel simples registra a empresa. Aluguel com crédito registra o banco. Aluguel com compra registra o cliente. | UC09 |
| US16 | Como banco, quero associar um contrato de crédito ao aluguel. | Só bancos concedem crédito. O contrato registra valor, juros mensais e parcelas. | UC10 |
| US17 | Como agente, quero cadastrar e manter automóveis. | Registra matrícula, ano, marca, modelo e placa. Matrícula e placa não se repetem. Só o proprietário altera ou exclui. Carro com pedidos não pode ser excluído. | UC11 |

## Consultas gerais

| ID | História | Critérios de aceitação | Caso de uso |
| --- | --- | --- | --- |
| US18 | Como usuário, quero ver os automóveis cadastrados e seus proprietários. | A lista mostra todos os automóveis. O cliente pede um carro disponível a partir da lista. | UC12 |
| US19 | Como usuário, quero consultar os contratos firmados. | O cliente vê só os próprios contratos. O agente vê todos. | UC13 |
