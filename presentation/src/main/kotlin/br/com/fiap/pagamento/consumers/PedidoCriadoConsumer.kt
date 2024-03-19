package br.com.fiap.pagamento.consumers

import br.com.fiap.pagamento.adapters.interfaces.PagamentoAdapter
import br.com.fiap.pagamento.constants.PEDIDO_CRIADO_FILA
import br.com.fiap.pagamento.requests.PedidoCriadoMsg
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@Component
class PedidoCriadoConsumer(
    private val pagamentoAdapter: PagamentoAdapter
) {

    @RabbitListener(queues = [PEDIDO_CRIADO_FILA])
    fun consumidor(mensagem: PedidoCriadoMsg) {
        println("Mensagem recebida: $mensagem")

        val result = pagamentoAdapter.criar(mensagem.valid())

        println("Pagamento result: $result")
    }
}