package br.com.fiap.pagamento.interfaces.usecases

import br.com.fiap.pagamento.models.Pagamento
import br.com.fiap.pagamento.models.mercadoPago.PagamentoCriado

fun interface FinalizarPagamentoUseCase {
    fun executar(pagamentoCriado: PagamentoCriado): Pagamento
}