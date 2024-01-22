package br.com.fiap.pagamento.interfaces.usecases

import br.com.fiap.pagamento.enums.PagamentoStatus
import br.com.fiap.pagamento.models.Pagamento

fun interface AlterarStatusPagamentoUseCase {
    fun executar(referenciaPedido: String, status: PagamentoStatus) : Pagamento
}
