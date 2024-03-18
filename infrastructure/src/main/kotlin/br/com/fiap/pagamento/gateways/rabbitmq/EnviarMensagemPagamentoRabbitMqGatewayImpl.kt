package br.com.fiap.pagamento.gateways.rabbitmq

import br.com.fiap.pagamento.exceptions.ProducerMessageException
import br.com.fiap.pagamento.interfaces.gateways.EnviarMensagemPagamentoGateway
import br.com.fiap.pagamento.models.messaging.PagamentoMsg
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.stereotype.Service

private const val ERROR_MESSAGE = "Erro de integração enviar mensagem para fila. Detalhes: %s"

@Service
class EnviarMensagemPagamentoRabbitMqGatewayImpl(
    private val rabbitTemplate: RabbitTemplate,
    private val pagamentoFinalizadoQueue: Queue,
    private val objectMapper: ObjectMapper
) : EnviarMensagemPagamentoGateway {

    override fun executar(nomeDaFila: String, mensagem: PagamentoMsg) {
        try {
            println("Enviando mensagem: $mensagem")
            rabbitTemplate.convertAndSend(pagamentoFinalizadoQueue.name, mensagem)
            println("Mensagem enviada: $mensagem")
        } catch (ex: Exception) {
            throw ProducerMessageException(String.format(ERROR_MESSAGE, "${ex.message} - ${ex.cause}"))
        }
    }
}
