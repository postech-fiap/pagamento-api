package br.com.fiap.pagamento.services

import br.com.fiap.pagamento.constants.PAGAMENTO_FINALIZADO_FILA
import br.com.fiap.pagamento.enums.PagamentoStatus
import br.com.fiap.pagamento.exceptions.ProducerMessageException
import br.com.fiap.pagamento.interfaces.gateways.EnviarMensagemPagamentoGateway
import br.com.fiap.pagamento.interfaces.gateways.PagamentoFinalizadoServiceI
import br.com.fiap.pagamento.interfaces.usecases.AlterarStatusPagamentoUseCase
import br.com.fiap.pagamento.models.Pagamento
import br.com.fiap.pagamento.models.messaging.PagamentoMsg
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional(rollbackFor = [ProducerMessageException::class])
class PagamentoFinalizadoService(
    private val alterarStatusPagamentoUseCase: AlterarStatusPagamentoUseCase,
    private val enviarMensagemPagamentoGateway: EnviarMensagemPagamentoGateway,
) : PagamentoFinalizadoServiceI {

    override fun atualizaEPostaMensagem(idPedido: Long, pagamentoStatus: PagamentoStatus): Pagamento {
        val pagamentoComNovoStatus = alterarStatusPagamentoUseCase.executar(
            idPedido,
            pagamentoStatus
        )

        enviarMensagemPagamentoGateway.executar(
            PAGAMENTO_FINALIZADO_FILA,
            PagamentoMsg(idPedido, pagamentoComNovoStatus.id!!, pagamentoComNovoStatus.status)
        )

        return pagamentoComNovoStatus
    }

}