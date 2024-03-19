# pagamento-api

Aplicação responsável por realizar a cobrança de um pedido gerado anteriormente.

## Executando com Docker
Com o `docker` instalado, execute os comandos:

Criar a network
```bash
docker network create pagamento-network
```

Subir o container do Mongo DB:
```bash
docker run --name=pagamento-api-mongo --network=pagamento-network -p 27017:27017 -d \
-e MONGO_DB_PORT=27017 -e MONGO_DB_NAME=pagamento_db -d mongo
```

Subir o container do rabbit mq:
```bash
docker run --name=rabbit-mq --network=pagamento-network -p 5672:5672 -p 15672:15672 -d \
  -e RABBITMQ_HOST=localhost \
  -e RABBITMQ_PORT=5672 \
  -e RABBITMQ_USER={{rabbit_user}} \
  -e RABBITMQ_PASS={{rabbit_pass}} \
  -d rabbitmq:3.9-management
```

Build da imagem da api:
```bash
docker build -t pagamento-api:latest .
```

Subir o container da api:
```bash
docker run --name=pagamento-api --network=pagamento-network -p 8080:8080 -d \
  -e MONGO_DB_HOST=pagamento-api-mongo \
  -e MONGO_DB_PORT=27017 \
  -e MONGO_DB_NAME=pagamento_db \
  -e MERCADO_PAGO_USER_ID={{mp_user}} \
  -e MERCADO_PAGO_EXTERNAL_ID={{mp_external_id}} \
  -e MERCADO_PAGO_TOKEN={{mp_token}}  \
  -e MERCADO_PAGO_WEBHOOK_URL={{webhook}} \
  -e RABBITMQ_HOST=rabbit-mq \
  -e RABBITMQ_PORT=5672 \
  -e RABBITMQ_USER={{rabbit_user}} \
  -e RABBITMQ_PASS={{rabbit_pass}} \
  -d pagamento-api
```

## Executando com Docker Compose
Com o `docker` instalado, execute o arquivo de docker-compose.yml, substituindo as envs pelos valores corretos.

### Variáveis de ambiente

- As variáveis do Mercado Pago estão presentes no documento já enviado por anexo nos Tech Challenges anteriores.

### Executar swagger
```sh
curl --location 'http://localhost:8080/swagger-ui/index.html'
```

## [Integração com Mercado Pago Api](README-MERCADOPAGO.md)

## Padrão SAGA Coreografada

Este projeto faz uso da SAGA coreografada.
Mais detalhes da justificativa, acessar o link no documento compartilhado.

### Desenho da arquitetura
Acessar o link no documento compartilhado.

## Relatórios OWASP ZAP
Acessar o link no documento compartilhado.
