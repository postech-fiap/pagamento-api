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

Build da imagem da api:
```bash
docker build -t pagamento-api:latest .
```

Subir o container da api:
```bash
docker run --name=pagamento-api --network=pagamento-network -p 8080:8080 -d \
  -e MONGO_DB_HOST=pagamento-api-mongo -e MONGO_DB_PORT=27017 -e MONGO_DB_NAME=pagamento_db \
  -e MERCADO_PAGO_USER_ID={{mp_user}} \
  -e MERCADO_PAGO_EXTERNAL_ID={{mp_external_id}} \
  -e MERCADO_PAGO_TOKEN={{mp_token}} \
  -e MERCADO_PAGO_WEBHOOK_URL={{webhook}} \
  -d pagamento-api
```

## Executando com Docker Compose
Com o `docker` instalado, execute o comando:

```bash
MONGO_DB_HOST={{db_host}} MONGO_DB_PORT={{db_port}} MONGO_DB_NAME=pagamento_db \
MERCADO_PAGO_USER_ID={{mp_user}} MERCADO_PAGO_EXTERNAL_ID={{mp_external_id}} MERCADO_PAGO_TOKEN={{mp_token}} MERCADO_PAGO_WEBHOOK_URL={{webhook}} \
docker compose up -d
```

### Variáveis de ambiente

- As variáveis estão presentes no documento já enviado por anexo nos Tech Challenges anteriores.

### Executar swagger
```sh
curl --location 'http://localhost:8080/swagger-ui/index.html'
```

## [Integração com Mercado Pago Api](README-MERCADOPAGO.md)
