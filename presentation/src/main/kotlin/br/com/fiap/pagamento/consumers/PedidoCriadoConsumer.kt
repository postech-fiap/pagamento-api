package br.com.fiap.pagamento.consumers

import br.com.fiap.pagamento.adapters.interfaces.PagamentoAdapter
import br.com.fiap.pagamento.constants.PEDIDO_CRIADO_FILA
import br.com.fiap.pagamento.requests.PedidoCriadoMsg
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.amqp.rabbit.annotation.RabbitHandler
import org.springframework.amqp.rabbit.annotation.RabbitListener
import org.springframework.stereotype.Component

@RabbitListener(queues = [PEDIDO_CRIADO_FILA])
class PedidoCriadoConsumer(
        private val pagamentoAdapter: PagamentoAdapter,
        private val objectMapper: ObjectMapper
) {
    @RabbitHandler
    private fun consumidor(mensagem: String) {

        val pedidoDto = objectMapper.readValue<PedidoCriadoMsg>(mensagem)
        pagamentoAdapter.criar(pedidoDto.valid())
    }
}