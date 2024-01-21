package br.com.fiap.pagamento.usecases

import br.com.fiap.pagamento.enums.PagamentoStatus
import br.com.fiap.pagamento.interfaces.PagamentoRepository
import br.com.fiap.pagamento.interfaces.usecases.AlterarStatusPagamentoUseCase
import br.com.fiap.pagamento.interfaces.usecases.ConsultarPagamentoUseCase
import br.com.fiap.pagamento.models.Pagamento

class AlterarStatusPagamentoUseCaseImpl(
    private val consultarPagamentoUseCase: ConsultarPagamentoUseCase,
    private val pagamentoRepository: PagamentoRepository
) : AlterarStatusPagamentoUseCase {

    override fun executar(pedidoId: Long, status: PagamentoStatus): Pagamento {
        val pagamento = consultarPagamentoUseCase.executar(pedidoId)

        return pagamentoRepository.alterarStatusPagamento(pagamento, status) }
}
