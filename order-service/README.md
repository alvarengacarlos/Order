# Order Service
## Descrição
Microserviço responsável por administrar os pedidos da aplicação.

Este serviço é executado em uma AWS Lambda com runtime Java 21.

Os recursos são provisionados com Terraform. Além disso é possível utilizar o SAM CLI para executar testes locais. Leia mais [aqui](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/terraform-support.html).

Para a persistência de dados é utilizado uma tabela do DynamoDB.

## Testes
Execute os testes unitários:
```bash
gradle test
# ou somente uma suite de testes
gradle test --tests com.alvarengacarlos.order.www.OrderServiceTest
```
