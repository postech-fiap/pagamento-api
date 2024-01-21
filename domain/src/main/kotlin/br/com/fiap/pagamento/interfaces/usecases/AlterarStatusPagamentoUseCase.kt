package br.com.fiap.pagamento.interfaces.usecases

import br.com.fiap.pagamento.enums.PagamentoStatus
import br.com.fiap.pagamento.models.Pagamento

fun interface AlterarStatusPagamentoUseCase {
    fun executar(pedidoId: Long, status: PagamentoStatus) : Pagamento
}
