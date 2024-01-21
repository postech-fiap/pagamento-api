package br.com.fiap.pagamento.interfaces.usecases

import br.com.fiap.pagamento.models.Pagamento

fun interface ConsultarPagamentoUseCase {
    fun executar(pedidoId: Long) : Pagamento
}
