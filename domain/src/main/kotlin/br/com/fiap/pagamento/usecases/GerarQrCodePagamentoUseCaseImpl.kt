package br.com.fiap.pagamento.usecases

import br.com.fiap.pagamento.interfaces.gateways.GerarQrCodePagamentoGateway
import br.com.fiap.pagamento.interfaces.usecases.GerarQrCodePagamentoUseCase
import br.com.fiap.pagamento.models.Pedido

class GerarQrCodePagamentoUseCaseImpl(
    private val gerarQrCodePagamentoGateway: GerarQrCodePagamentoGateway
) : GerarQrCodePagamentoUseCase {

    override fun executar(pedido: Pedido) = gerarQrCodePagamentoGateway.executar(pedido)
}
