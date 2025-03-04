# Account Service
## Descrição
Microserviço responsável por administrar as contas da aplicação sejam elas dos funcionários ou dos clientes.

Este serviço é executado em uma AWS Lambda com runtime Java 21.

Os recursos são provisionados com Terraform. Além disso é possível utilizar o SAM CLI para executar testes locais. Leia mais [aqui](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/terraform-support.html).

Para a persistência de dados é utilizado uma tabela do DynamoDB.

## Testes
Execute os testes unitários:
```bash
mvn test
# ou somente uma suite de testes
mvn -Dtest=com.alvarengacarlos.order.www.EmployeeServiceTest test
```
