package br.com.fiap.pagamento.interfaces.gateways

import br.com.fiap.pagamento.models.messaging.PagamentoMsg

interface EnviarMensagemPagamentoGateway {
    fun executar(nomeDaFila: String, mensagem: PagamentoMsg)
}
