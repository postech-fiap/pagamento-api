package br.com.fiap.pagamento.interfaces.gateways

import br.com.fiap.pagamento.models.Pagamento
import br.com.fiap.pagamento.models.Pedido

fun interface GerarQrCodePagamentoGateway {

    fun executar(pedido: Pedido): Pagamento
}
