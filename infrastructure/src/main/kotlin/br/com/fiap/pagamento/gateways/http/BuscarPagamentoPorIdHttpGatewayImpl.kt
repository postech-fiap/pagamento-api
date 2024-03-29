package br.com.fiap.pagamento.gateways.http

import br.com.fiap.pagamento.dtos.MercadoPagoResponseMerchantOrders
import br.com.fiap.pagamento.exceptions.IntegracaoAPIException
import br.com.fiap.pagamento.interfaces.gateways.BuscarPagamentoPorIdGateway
import br.com.fiap.pagamento.models.mercadoPago.MerchantOrders
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestTemplate
import org.springframework.web.util.UriComponentsBuilder

private const val HEADER_NAME_AUTH = "Authorization"
private const val ERROR_MESSAGE = "Erro de integração para buscar o pagamento. Detalhes: %s"

class BuscarPagamentoPorIdHttpGatewayImpl(
        private val restTemplate: RestTemplate,
        private val mercadoPagoApiMerchantOrdersPagamento: String,
        private val mercadoPagoToken: String
) : BuscarPagamentoPorIdGateway {

    override fun executar(id: String): MerchantOrders {
        val url = UriComponentsBuilder.fromUriString(mercadoPagoApiMerchantOrdersPagamento)
            .buildAndExpand(id)
            .toUriString()

        val entity = HttpEntity<Unit>(buildHeaders())

        try {
            val response =
                restTemplate.exchange(url, HttpMethod.GET, entity, MercadoPagoResponseMerchantOrders::class.java)

            if (response.statusCode != HttpStatus.OK) {
                throw IntegracaoAPIException(
                        String.format(
                                ERROR_MESSAGE,
                                "[status_code: " + "${response.statusCode}"
                        )
                )
            }

            requireNotNull(response.body) { "A resposta não deve ser nula " }

            return response.body!!.toModel()

        } catch (ex: IntegracaoAPIException) {
            throw ex
        } catch (ex: Exception) {
            throw IntegracaoAPIException(String.format(ERROR_MESSAGE, "${ex.message} - ${ex.cause}"))
        }
    }

    private fun buildHeaders(): HttpHeaders {
        val headers = HttpHeaders()
        headers[HEADER_NAME_AUTH] = mercadoPagoToken
        return headers
    }
}