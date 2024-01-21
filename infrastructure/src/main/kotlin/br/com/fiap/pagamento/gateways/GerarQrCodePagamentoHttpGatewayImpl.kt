package br.com.fiap.pagamento.gateways

import br.com.fiap.pagamento.dtos.MercadoPagoOrdemDto
import br.com.fiap.pagamento.dtos.MercadoPagoResponseOrdemDto
import br.com.fiap.pagamento.interfaces.gateways.GerarQrCodePagamentoGateway
import br.com.fiap.pagamento.models.Pagamento
import br.com.fiap.pagamento.exceptions.IntegracaoAPIException
import br.com.fiap.pagamento.models.Pedido
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestTemplate
import kotlin.text.*

private const val HEADER_NAME_AUTH = "Authorization"
private const val ERROR_MESSAGE_QRCODE = "Erro de integração para gerar o pagamento. Detalhes: %s"

class GerarQrCodePagamentoHttpGatewayImpl(
    private val restTemplate: RestTemplate,
    private val mercadoPagoApiQrCodeEndpoint: String,
    private val mercadoPagoToken: String,
    private val mercadoPagoWebhookUrl: String
) : GerarQrCodePagamentoGateway {

    override fun executar(pedido: Pedido): Pagamento {
        try {
            val response = restTemplate.postForEntity(
                mercadoPagoApiQrCodeEndpoint,
                buildHttpEntity(pedido),
                MercadoPagoResponseOrdemDto::class.java
            )

            if (response.statusCode != HttpStatus.CREATED) {
                throw IntegracaoAPIException(
                    String.format(
                        ERROR_MESSAGE_QRCODE,
                        "[status_code: " + "${response.statusCode}"
                    )
                )
            }

            requireNotNull(response.body) { "A resposta não deve ser nula " }
            return response.body!!.toModel(pedido)

        } catch (ex: IntegracaoAPIException) {
            throw ex
        } catch (ex: Exception) {
            throw IntegracaoAPIException(String.format(ERROR_MESSAGE_QRCODE, "${ex.message} - ${ex.cause}"))
        }
    }

    private fun buildHttpEntity(pedido: Pedido) =
        HttpHeaders()
            .let {
                it.set(HEADER_NAME_AUTH, mercadoPagoToken)
                HttpEntity(MercadoPagoOrdemDto.fromDto(pedido, mercadoPagoWebhookUrl), it)
            }
}

