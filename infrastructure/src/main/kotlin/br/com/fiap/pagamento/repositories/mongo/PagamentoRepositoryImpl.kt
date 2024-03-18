package br.com.fiap.pagamento.repositories.mongo

import br.com.fiap.pagamento.entities.PagamentoEntity
import br.com.fiap.pagamento.enums.PagamentoStatus
import br.com.fiap.pagamento.exceptions.BaseDeDadosException
import br.com.fiap.pagamento.interfaces.PagamentoRepository
import br.com.fiap.pagamento.models.Pagamento

private const val ERROR_MESSAGE_UPDATE_STATUS = "Erro ao atualizar o status do pagamento. Detalhes: %s"
private const val ERROR_MESSAGE_SAVE = "Erro ao salvar o pagamento. Detalhes: %s"
private const val ERROR_MESSAGE_FIND = "Erro ao buscar o pagamento. Detalhes: %s"

class PagamentoRepositoryImpl(private val pagamentoMongoRepository: PagamentoMongoRepository) : PagamentoRepository {

    override fun salvar(pagamento: Pagamento): Pagamento {
        try {
            val entity = PagamentoEntity.fromModel(pagamento)

            return pagamentoMongoRepository.save(entity).toModel()
        } catch (ex: Exception) {
            throw BaseDeDadosException(
                    String.format(ERROR_MESSAGE_SAVE, ex.message)
            )
        }
    }

    override fun consultarPorPedido(idPedido: Long): Pagamento? {
        return try {
            pagamentoMongoRepository.findByPedidoId(idPedido)?.toModel()
        } catch (ex: Exception) {
            throw BaseDeDadosException(String.format(ERROR_MESSAGE_FIND, ex.message))
        }
    }

    override fun alterarStatusPagamento(pagamento: Pagamento, status: PagamentoStatus): Pagamento {
        try {
            val newPagamentoStatus = pagamento.copy(status = status)
            val entity = PagamentoEntity.fromModel(newPagamentoStatus)

            return pagamentoMongoRepository.save(entity).toModel()
        } catch (ex: Exception) {
            throw BaseDeDadosException(
                    String.format(ERROR_MESSAGE_UPDATE_STATUS, ex.message)
            )
        }
    }
}