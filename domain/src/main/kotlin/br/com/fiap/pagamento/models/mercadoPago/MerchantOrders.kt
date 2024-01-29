package br.com.fiap.pagamento.models.mercadoPago

import br.com.fiap.pagamento.enums.PagamentoStatus
import java.math.BigDecimal

private const val STATUS_APROVADO = "approved"

data class MerchantOrders(
        val elements: List<Elements>? = emptyList()
) {
    data class Elements(
            val id: Long,
            val status: String,
            val externalReference: String,
            val payments: List<Payment>? = emptyList()
    ) {
        data class Payment(
                val id: Long,
                val transactionAmount: BigDecimal,
                val totalPaidAmount: BigDecimal,
                val status: String,
                val statusDetail: String? = null,
                val dateApproved: String,
                val dateCreated: String,
                val lastModified: String? = null,
                val amountRefunded: BigDecimal? = null
        ) {

            fun calcularStatusPagamento() =
                    if (STATUS_APROVADO.equals(this.status, true)) PagamentoStatus.APROVADO
                    else PagamentoStatus.REPROVADO
        }
    }

    fun obterUltimoPagamento(pedidoId: String) =
            let {
                val payment = this.elements
                        ?.first { it.externalReference == pedidoId }
                        ?.payments?.get(0)

                requireNotNull(payment) { "O pagamento não deve ser nulo" }
                payment
            }

    fun obterIdDoPedido() =
            let {
                val externalReference =
                        this.elements?.get(0)
                                ?.externalReference

                requireNotNull(externalReference) { "O id do pedido não deve ser nulo" }
                externalReference
            }
}
