package br.com.fiap.pagamento.adapters.interfaces

import br.com.fiap.pagamento.requests.PagamentoCriadoRequest
import br.com.fiap.pagamento.requests.PedidoCriadoRequest
import br.com.fiap.pagamento.models.Pagamento

interface PagamentoAdapter {

    fun criar(request: PedidoCriadoRequest): Pagamento
    fun finalizar(request: PagamentoCriadoRequest): Pagamento
}
