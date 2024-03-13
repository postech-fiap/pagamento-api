package br.com.fiap.pagamento.interfaces.gateways

import br.com.fiap.pagamento.enums.PagamentoStatus
import br.com.fiap.pagamento.models.Pagamento

interface PagamentoFinalizadoServiceI {

    fun atualizaEPostaMensagem(idPedido: Long, pagamentoStatus: PagamentoStatus): Pagamento
}
