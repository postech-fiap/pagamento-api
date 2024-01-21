package br.com.fiap.pagamento.usecases

import br.com.fiap.pagamento.exceptions.RecursoNaoEncontradoException
import br.com.fiap.pagamento.interfaces.PagamentoRepository
import br.com.fiap.pagamento.interfaces.usecases.ConsultarPagamentoUseCase
import br.com.fiap.pagamento.models.Pagamento
import java.util.*

class ConsultarPagamentoUseCaseImpl(
    private val pagamentoRepository: PagamentoRepository
) : ConsultarPagamentoUseCase {

    override fun executar(pedidoId: Long): Pagamento {
        return Optional.ofNullable(pagamentoRepository.consultarPorPedido(pedidoId))
            .orElseThrow { RecursoNaoEncontradoException("Pagamento não encontrado") }
    }
}
