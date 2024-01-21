# pagamento-api

Aplicação responsável por realizar a cobrança de um pedido gerado anteriormente.

## Executando com Docker

Subir o sonarqube
```bash
docker run -d --name sonarqube -p 9000:9000
```

Análise do sonarqube no projeto (cobertura de código)
Obs.: Para executar, você deve informar as variáveis de ambiente SONAR_HOST_URL e SONAR_TOKEN (Serão enviadas no documento).

```bash
gradle sonar
```

E acesse http://localhost:9000

![Alt text](https://github.com/postech-fiap/pagamento/blob/feature/add-pagamento-api-test/docs/assets/Cobertura_de_codigo.png?raw=true)

## Executando com Docker

Com o `docker` instalado, execute o comando:
```bash
docker-compose up -d
```

### Variáveis de ambiente

- As variáveis estão presentes no documento que será enviado por anexo.

### Executar swagger
```sh
curl --location 'http://localhost:30000/swagger-ui/index.html'
```

### Executar swagger (load balancer)
```sh
curl --location 'http://localhost:8080/swagger-ui/index.html'
```

## [Integração com Mercado Pago Api](README-MERCADOPAGO.md)
