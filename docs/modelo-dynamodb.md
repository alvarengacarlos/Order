# Modelo Amazon DynamoDB

| Partition Key       | Sort Key              | Attributes     |                      |                              |            |                                           |
| ------------------- | --------------------- | -------------- | -------------------- | ---------------------------- | ---------- | ----------------------------------------- |
|                     |                       | **nome**       | **usuario**          | **senha**                    | **salto**  | **papel**                                 |
| Conta#1             | Funcionario           | Joao da Silva  | joao                 | $a324sth...                  | 123456     | ATENDENTE                                 |
|                     |                       | **nome**       | **telefone**         |                              |            |                                           |
| Conta#2             | Consumidor            | Maria Santos   | (00)00000-0000       |                              |            |                                           |
|                     |                       | cidade         | bairro               | rua                          | numero     | referencia                                |
| Conta#2             | Endereco#1            | Porto Alegre   | Laranjas             | Cubo Magico                  | 402        | proximo a igreja                          |
|                     |                       | **nome**       | **cnpj**             |                              |            |                                           |
| Inquilino#1         | Inquilino             | Minha comida   | 00.000.000/0001-00   |                              |            |                                           |
|                     |                       | **nome**       | **telefone**         |                              |            |                                           |
| Inquilino#1         | Dono                  | Fernanda Abreu | (00)00000-0000       |                              |            |                                           |
|                     |                       | **valor**      |                      |                              |            |                                           |
| Inquilino#1         | Assinatura            | 1000.00        |                      |                              |            |                                           |
|                     |                       | **data**       | **formaDepagamento** | **identificadorDoPagamento** |            |                                           |
| Inquilino#1         | PagamentoDaAssinatura | 2000-01-01     | PIX                  | e$25nshtu...                 |            |                                           |
|                     |                       | **cidade**     | **bairro**           | **rua**                      | **numero** | **referencia**                            |
| Inquilino#1         | Endereco              | referencia     | Jardim               | Primavera                    | 330        |                                           |
|                     |                       | **nome**       | **descricao**        | **preco**                    |            |                                           |
| Produto#1           | Produto               | Hamburger      | com alface...        | 10.00                        |            |                                           |
|                     |                       | **produtos**   | **total**            |                              |            |                                           |
| CarrinhoDeCompras#1 | CarrinhoDeCompras     | [1,2]          | 20.00                |                              |            |                                           |
|                     |                       | **produtos**   | **total**            | **formaDePagamento**         | **estado** | **endereco**                              |
| Pedido#1            | Pedido                | [1,2]          | 20.00                | PIX                          | ACEITO     | {cidade, bairro, rua, numero, referencia} |
| Pedido#1            | HistoricoDoEstado     | ACEITO         | 2000-01-02           |                              |            |                                           |
