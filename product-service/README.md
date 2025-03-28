# Order Service
## Descrição
Microserviço responsável por administrar os produtos da aplicação.

Este serviço é executado em uma AWS Lambda com runtime Node.JS 22.

Os recursos são provisionados com Terraform. Além disso é possível utilizar o SAM CLI para executar testes locais. Leia mais [aqui](https://docs.aws.amazon.com/serverless-application-model/latest/developerguide/terraform-support.html).

Para a persistência de dados é utilizado uma tabela do DynamoDB.

## Testes
Execute os testes unitários:
```bash
npm run test:component
```
