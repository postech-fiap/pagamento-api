package br.com.fiap.pagamento.interfaces.usecases

import br.com.fiap.pagamento.models.Pagamento
import br.com.fiap.pagamento.models.Pedido

fun interface GerarQrCodePagamentoUseCase {
    fun executar(pedido: Pedido): Pagamento
}