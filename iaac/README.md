# IaaC - Recursos compartilhados
## Descrição
Provisiona os recursos compartilhados entre os microserviços. São eles:
- Amazon API Gateway
- Amazon DynamoDB
- Amazon S3 Bucket

## Implantação
Inicialize o Terraform:
```bash
terraform init
```

Provisione os recursos:
```bash
terraform apply
```

> **Dica:** Copie o output `http_api_id`.
