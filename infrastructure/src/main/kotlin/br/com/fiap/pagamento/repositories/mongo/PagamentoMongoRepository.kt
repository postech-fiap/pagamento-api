package br.com.fiap.pagamento.repositories.mongo

import br.com.fiap.pagamento.entities.PagamentoEntity
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query

interface PagamentoMongoRepository : MongoRepository<PagamentoEntity, Long> {

    @Query("{referenciaPedido:?0}")
    fun findByPedidoId(referenciaPedido: String) : PagamentoEntity?

}
