# IaaC - Serviço de conta
## Descrição
Provisiona os recursos do microserviço de conta.

## Implantação
Crie o pacote:
```bash
cd .. &&\
mvn clean package &&\
cd iaac
```

Suba o pacote no Bucket:
```bash
aws s3 cp ../target/account-service-1.0.0.jar s3://com-alvarengacarlos-order-www/account-service-1.0.0.jar
```

Inicialize o Terraform:
```bash
terraform init
```

Provisione os recursos:
```bash
terraform apply -var "http_api_id=SEU_API_ID" -var "jwt_secret=SEU_JWT_SECRET"
```
