# Especificação de requisitos de sistema

> **Atenção:** Para as extensões `.plantuml` e `.drawio` use um aplicativo que de suporte.

## Introdução
Esta plataforma tem como objetivo gerenciar pedidos de restaurantes.

## Requisitos funcionais
- Como administrador do restaurante eu quero consultar o histórico de pedidos feitos por consumidor e por data, podendo rastrear desde o início do pedido até a sua entrega.

- Como administrador do restaurante eu quero visualizar minha assinatura da plataforma, assim como os pagamentos.

- Como administrador do restaurante eu quero cadastrar, editar, ativar, desativar e remover os meus funcionários, sendo eles atendentes, cozinheiros e entregadores.

- Como administrador do restaurante eu quero cadastrar, editar e remover os produtos que meu restaurante oferece.

- Como consumidor, eu quero acessar a plataforma sem me identificar para olhar o catálago de produtos. Além disso, eu quero pesquisar por produtos pelos seus nomes.

- Como consumidor, eu quero me identificar com o uso do meu número de telefone e meu nome. Além disso, quando necessário quero alterar ambos.

- Como consumidor, eu quero fazer um pedido com os produtos que eu selecionei.

- Como consumidor, eu quero escolher a forma de pagamento podendo ser mais de uma.

- Como consumidor, eu quero escolher retirar no local ou informar meu endereço para receber em minha casa.

- Como consumidor, eu quero consultar o estado do meu pedido.

- Como consumidor, eu quero consultar o meu histórico de pedidos feitos, por data.

- Eu como funcionario do restaurante quero alterar minhas senha quando necessário.

- Como atendente eu quero receber o pedido feito pelo consumidor e mudar o estado do pedido para aceito.

- Como atendente eu quero receber o pedido feito pelo consumidor e mudar o estado do pedido para negado. Além disso, quero informar por meio de uma mensagem o motivo da negação.


- Como atendente eu quero mudar o estado do pedido para pago e depois para entregue quando o consumidor for retirar o produto no local.

- Como atendente eu quero consultar o histórico de pedidos feitos, por consumidor e por data.

- Como cozinheiro eu quero receber o pedido aceito pelo atendente e mudar o estado do pedido para em preparo.

- Como cozinheiro eu quero mudar o estado do pedido para preparado quando ele estiver pronto.

- Como entregador eu quero receber o pedido preparado pelo cozinheiro e mudar seu estado para em entrega.

- Como entregador eu quero saber o nome da pessoa que irá receber e qual será o endereço de entrega. Além disso, eu quero  saber qual será a forma de pagamento e o valor total para que eu poça realizar a cobrança no local.

- Como entregador eu quero mudar o estado do pedido para pago e depois para entregue.

- Como entregador eu quero consultar o meu histórico de entregas feitas, por data.

- Como administrador da plataforma eu quero cadastrar e listar restaurantes(inquilinos).

- Como administrador da plataforma eu quero marcar e acompanhar os pagamentos feitos pelos utilizadores da plataforma, são eles, restaurantes (inquilinos).

- Como administrador da plataforma eu quero criar o contrato/assinatura que define os valores a serem pagos entre plataforma e utilizadores da plataforma, são eles, restaurantes (inquilinos).

- Como administrador eu quero poder bloquear e desbloquear o acesso por parte dos utilizadores da plataforma, são eles, restaurantes (inquilinos).

## Requisitos não funcionais
- A plataforma deve identificar o consumidor por meio do seu número de telefone e nome.

- A plataforma deve ser capaz de suportar 50 usuários ao mesmo tempo.

- A plataforma deve validar o número do telefone do consumidor por meio de um código.

- A plataforma deve funcionar no ambiente web.

- A plataforma deve mostrar somente as informações necessárias para cada tipo de persona.

- A plataforma deve ser focada na experiência do usuário em dispositívos móveis como celulares e tablets.

- A plataforma deve funcionar com a ideia de multi inquilinos com uma base de dados compartilhada para os restaurantes que irão utilizar a plataforma.

- A plataforma deve garantir que somente o consumidor tenha acesso aos seus dados.

- A plataforma deve trabalhar no modelo de assinatura onde os pagamentos serão mensais.

- As senhas devem ser guardadas em forma de hash com um salt.

- Os funcionarios cadastrados devem conter uma senha padrão que deverá ser alterada no primeiro acesso a plataforma.

### Entidades
[Entidades](./entidades.plantuml).

### Fluxograma
[Fluxograma](./fluxograma.plantuml).

### Subdominios
[Subdominios](./subdominios.plantuml)

## Operações de sistema
- [Rastrear pedido](./comunicacao-entre-servicos/rastrear-pedido.plantuml).
- [Cadastrar funcionário](./comunicacao-entre-servicos/cadastrar-funcionario.plantuml).
- [Remover funcionário](./comunicacao-entre-servicos/remover-funcionario.plantuml).
- [Editar funcionário](./comunicacao-entre-servicos/editar-funcionario.plantuml).
- [Cadastrar produto](./comunicacao-entre-servicos/cadastrar-produto.plantuml).
- [Remover produto](./comunicacao-entre-servicos/remover-produto.plantuml).
- [Editar produto](./comunicacao-entre-servicos/editar-produto.plantuml).
- [Listar produtos](./comunicacao-entre-servicos/listar-produtos.plantuml).
- [Pesquisar produtos](./comunicacao-entre-servicos/pesquisar-produto.plantuml).
- [Cadastrar-me](./comunicacao-entre-servicos/cadastrar-me.plantuml).
- [Validar número de telefone com código](./comunicacao-entre-servicos/validar-numero-de-telefone-com-codigo.plantuml).
- [Adicionar produto ao carrinho](./comunicacao-entre-servicos/adicionar-produto-ao-carrinho.plantuml).
- [Remover produto do carrinho](./comunicacao-entre-servicos/remover-produto-do-carrinho.plantuml).
- [Fazer pedido](./comunicacao-entre-servicos/fazer-pedido.plantuml).
- [Listar pedidos](./comunicacao-entre-servicos/listar-pedidos.plantuml).
- [Mudar estado do pedido](./comunicacao-entre-servicos/mudar-estado-do-pedido.plantuml).
- [Cadastrar restaurante (inquilinos)](./comunicacao-entre-servicos/cadastrar-restaurante.plantuml).
- [Listar restaurantes (inquilinos)](./comunicacao-entre-servicos/listar-restaurantes.plantuml).
- [Está autorizado a realizar a operação](./comunicacao-entre-servicos/esta-autorizado-a-realizar-operacao.plantuml).
- [Alterar senha do funcionario](./comunicacao-entre-servicos/alterar-senha-do-funcionario.plantuml).
- [Alterar nome do consumidor](./comunicacao-entre-servicos/alterar-nome-do-consumidor.plantuml).
- [Alterar telefone do consumidor](./comunicacao-entre-servicos/alterar-telefone-do-consumidor.plantuml).
- [Ativar funcionario](./comunicacao-entre-servicos/ativar-funcionario.plantuml).
- [Desativar funcionario](./comunicacao-entre-servicos/desativar-funcionario.plantuml).
- [Criar pagamento de assinatura](./comunicacao-entre-servicos/criar-pagamento-de-assinatura.plantuml).
- [Criar assinatura](./comunicacao-entre-servicos/criar-assinatura.plantuml).
- [Bloquear acesso do restaurante](./comunicacao-entre-servicos/bloquear-acesso-do-restaurante.plantuml).
- [Autenticação do funcionário](./comunicacao-entre-servicos/autenticacao-do-funcionario.plantuml).

### UI
[UI](./ui.drawio).

## Referências
[Introducing Assemblage - a microservice architecture definition process](https://microservices.io/post/architecture/2023/02/09/assemblage-architecture-definition-process.html#step-2-defining-subdomains).