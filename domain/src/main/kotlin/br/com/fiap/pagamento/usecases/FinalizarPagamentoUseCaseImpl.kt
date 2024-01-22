package br.com.fiap.pagamento.usecases

import br.com.fiap.pagamento.interfaces.gateways.AtualizarPedidoGateway
import br.com.fiap.pagamento.interfaces.gateways.BuscarPagamentoPorIdGateway
import br.com.fiap.pagamento.interfaces.usecases.AlterarStatusPagamentoUseCase
import br.com.fiap.pagamento.interfaces.usecases.FinalizarPagamentoUseCase
import br.com.fiap.pagamento.models.Pagamento
import br.com.fiap.pagamento.models.mercadoPago.PagamentoCriado

class FinalizarPagamentoUseCaseImpl(
    private val buscarPagamentoPorIdGateway: BuscarPagamentoPorIdGateway,
    private val alterarStatusPagamentoUseCase: AlterarStatusPagamentoUseCase,
    private val atualizarPedidoGateway: AtualizarPedidoGateway
) : FinalizarPagamentoUseCase {

    override fun executar(pagamentoCriado: PagamentoCriado): Pagamento {
        val ordemComPagamentoMercadoPago = buscarPagamentoPorIdGateway.executar(pagamentoCriado.data.id)

        val pedidoId = ordemComPagamentoMercadoPago.obterIdDoPedido()

        val pagamentoMercadoPago = ordemComPagamentoMercadoPago.obterUltimoPagamento(pedidoId)

        val pagamentoComNovoStatus = alterarStatusPagamentoUseCase.executar(
            pedidoId,
            pagamentoMercadoPago.calcularStatusPagamento()
        )

        atualizarPedidoGateway.executar(pedidoId.toLong(), pagamentoComNovoStatus.id!!, pagamentoComNovoStatus.status)

        return pagamentoComNovoStatus
    }

}
