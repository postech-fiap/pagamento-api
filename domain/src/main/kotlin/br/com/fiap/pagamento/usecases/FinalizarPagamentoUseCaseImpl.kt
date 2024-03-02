package br.com.fiap.pagamento.usecases

import br.com.fiap.pagamento.interfaces.gateways.BuscarPagamentoPorIdGateway
import br.com.fiap.pagamento.interfaces.gateways.PagamentoFinalizadoServiceI
import br.com.fiap.pagamento.interfaces.usecases.FinalizarPagamentoUseCase
import br.com.fiap.pagamento.models.Pagamento
import br.com.fiap.pagamento.models.mercadoPago.PagamentoCriado

class FinalizarPagamentoUseCaseImpl(
        private val buscarPagamentoPorIdGateway: BuscarPagamentoPorIdGateway,
        private val pagamentoFinalizadoService: PagamentoFinalizadoServiceI
) : FinalizarPagamentoUseCase {

    override fun executar(pagamentoCriado: PagamentoCriado): Pagamento {
        val ordemComPagamentoMercadoPago = buscarPagamentoPorIdGateway.executar(pagamentoCriado.data.id)

        val idPedido = ordemComPagamentoMercadoPago.obterIdDoPedido()

        val pagamentoMercadoPago = ordemComPagamentoMercadoPago.obterUltimoPagamento(idPedido)

        return pagamentoFinalizadoService.atualizaEPostaMensagem(idPedido.toLong(), pagamentoMercadoPago.calcularStatusPagamento())
    }

}
