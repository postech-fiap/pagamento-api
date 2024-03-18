package br.com.fiap.pagamento.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Configuration

@Configuration
class EndpointConfig(
    @Value("\${mercado-pago-api.generate-qrcode}")
    val mercadoPagoQrcodeEndpoint: String,
    @Value("\${mercado-pago-api.merchant-orders-pagamento}")
    val mercadoPagoOrdersPagamentoEndpoint: String,
    @Value("\${secrets.mercado-pago.token}")
    val mercadoPagotoken: String,
    @Value("\${secrets.mercado-pago.webhook-url}")
    val mercadoPagowebhookUrl: String,
)
