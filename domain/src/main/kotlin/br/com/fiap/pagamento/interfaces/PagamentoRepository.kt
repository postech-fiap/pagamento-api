package br.com.fiap.pagamento.interfaces

import br.com.fiap.pagamento.enums.PagamentoStatus
import br.com.fiap.pagamento.models.Pagamento

interface PagamentoRepository {

    fun alterarStatusPagamento(pagamento: Pagamento, status: PagamentoStatus): Pagamento
    fun salvar(pagamento: Pagamento): Pagamento
    fun consultarPorPedido(idPedido: Long): Pagamento?
}
