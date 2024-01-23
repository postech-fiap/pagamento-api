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

        val referenciaPedido = ordemComPagamentoMercadoPago.obterIdDoPedido()

        val pagamentoMercadoPago = ordemComPagamentoMercadoPago.obterUltimoPagamento(referenciaPedido)

        val pagamentoComNovoStatus = alterarStatusPagamentoUseCase.executar(
            referenciaPedido,
            pagamentoMercadoPago.calcularStatusPagamento()
        )

        atualizarPedidoGateway.executar(referenciaPedido, pagamentoComNovoStatus.id!!, pagamentoComNovoStatus.status)

        return pagamentoComNovoStatus
    }

}
