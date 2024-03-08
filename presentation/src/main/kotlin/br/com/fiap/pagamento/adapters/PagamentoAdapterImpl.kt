package br.com.fiap.pagamento.adapters

import br.com.fiap.pagamento.adapters.interfaces.PagamentoAdapter
import br.com.fiap.pagamento.exceptions.RecursoJaExisteException
import br.com.fiap.pagamento.exceptions.RecursoNaoEncontradoException
import br.com.fiap.pagamento.interfaces.PagamentoRepository
import br.com.fiap.pagamento.interfaces.usecases.ConsultarPagamentoUseCase
import br.com.fiap.pagamento.requests.PagamentoCriadoRequest
import br.com.fiap.pagamento.interfaces.usecases.FinalizarPagamentoUseCase
import br.com.fiap.pagamento.interfaces.usecases.GerarQrCodePagamentoUseCase
import br.com.fiap.pagamento.models.Pagamento
import br.com.fiap.pagamento.requests.PedidoCriadoMsg

class PagamentoAdapterImpl(
    private val gerarQrCodePagamentoUseCase: GerarQrCodePagamentoUseCase,
    private val pagamentoRepository: PagamentoRepository,
    private val consultarPagamentoUseCase: ConsultarPagamentoUseCase,
    private val finalizarPagamentoUseCase: FinalizarPagamentoUseCase,
) : PagamentoAdapter {

    override fun criar(request: PedidoCriadoMsg): Pagamento {
        return try {
            consultarPagamentoUseCase.executar(request.idPedido!!)
            throw RecursoJaExisteException("Pagamento já existe para o id do pedido: ${request.idPedido}")
        } catch (ex: RecursoNaoEncontradoException) {
            gerarQrCodePagamentoUseCase.executar(request.toModel())
                .let { pagamentoRepository.salvar(it) }
        }
    }

    override fun finalizar(request: PagamentoCriadoRequest) =
        finalizarPagamentoUseCase.executar(request.toModel())
}
