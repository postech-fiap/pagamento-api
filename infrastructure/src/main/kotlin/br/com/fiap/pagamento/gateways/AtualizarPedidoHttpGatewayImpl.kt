package br.com.fiap.pagamento.gateways

import br.com.fiap.pagamento.enums.PagamentoStatus
import br.com.fiap.pagamento.exceptions.IntegracaoAPIException
import br.com.fiap.pagamento.interfaces.gateways.AtualizarPedidoGateway
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.web.client.RestTemplate

private const val ERROR_MESSAGE = "Erro de integração para atualizar o pedido. Detalhes: %s"

class AtualizarPedidoHttpGatewayImpl(
    private val restTemplate: RestTemplate,
    private val pedidosApiEndpoint: String
) : AtualizarPedidoGateway {

    override fun executar(referenciaPedido: String, idPagamento: String, statusPagamento: PagamentoStatus) {

        try {

            val response =
                restTemplate.exchange(
                    pedidosApiEndpoint,
                    HttpMethod.PUT,
                    buildHttpEntity(referenciaPedido, idPagamento, statusPagamento),
                    String::class.java
                )

            if (response.statusCode != HttpStatus.OK) {
                throw IntegracaoAPIException(
                    String.format(
                        ERROR_MESSAGE,
                        "[status_code: " + "${response.statusCode}"
                    )
                )
            }

        } catch (ex: IntegracaoAPIException) {
            throw ex
        } catch (ex: Exception) {
            throw IntegracaoAPIException(String.format(ERROR_MESSAGE, "${ex.message} - ${ex.cause}"))
        }
    }

    data class AtualizarPedidoRequest(
        private val referenciaPedido: String,
        private val idPagamento: String,
        private val statusPagamento: String
    )

    private fun buildHttpEntity(referenciaPedido: String, idPagamento: String, statusPagamento: PagamentoStatus) =
        HttpHeaders()
            .let {
                it.set("Content-Type", "application/json")
                HttpEntity(AtualizarPedidoRequest(referenciaPedido, idPagamento, statusPagamento.toString()))
            }
}