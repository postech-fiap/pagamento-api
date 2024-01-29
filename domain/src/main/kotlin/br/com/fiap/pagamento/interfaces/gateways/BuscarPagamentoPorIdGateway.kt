package br.com.fiap.pagamento.interfaces.gateways

import br.com.fiap.pagamento.models.mercadoPago.MerchantOrders

fun interface BuscarPagamentoPorIdGateway {
    fun executar(id: String): MerchantOrders
}
