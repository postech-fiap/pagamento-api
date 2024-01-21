package br.com.fiap.pagamento.interfaces.gateways

import br.com.fiap.pagamento.enums.PagamentoStatus

fun interface AtualizarPedidoGateway {
    fun executar(idPedido: Long, idPagamento: String, statusPagamento: PagamentoStatus)
}
