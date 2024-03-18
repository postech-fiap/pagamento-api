package br.com.fiap.pagamento.config

import br.com.fiap.pagamento.constants.PAGAMENTO_FINALIZADO_FILA
import br.com.fiap.pagamento.constants.PEDIDO_CRIADO_FILA
import org.springframework.amqp.core.AmqpAdmin
import org.springframework.amqp.core.Queue
import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EntityScan(basePackages = ["br.com.fiap.pagamento.*"])
class RabbitMqConfig(
    val rabbitTemplate: RabbitTemplate,
    val amqpAdmin: AmqpAdmin
) {

    @Bean
    fun pagamentoFinalizadoQueue(): Queue {
        return Queue(PAGAMENTO_FINALIZADO_FILA, false)
    }

    @Bean
    fun pagamentoFinalizadoCriarFila(pagamentoFinalizadoQueue: Queue): String? {
        return amqpAdmin.declareQueue(pagamentoFinalizadoQueue)
    }

    // ========= Descomentar para subir a app local // =========
//    @Bean
//    fun pedidoCriadoQueue(): Queue {
//        return Queue(PEDIDO_CRIADO_FILA, false)
//    }
//
//    @Bean
//    fun pedidoCriado(pedidoCriadoQueue: Queue): String? {
//        return amqpAdmin.declareQueue(pedidoCriadoQueue)
//    }
}
