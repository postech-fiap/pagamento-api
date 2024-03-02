package br.com.fiap.pagamento.requests

import br.com.fiap.pagamento.interfaces.Model
import br.com.fiap.pagamento.models.Item
import br.com.fiap.pagamento.models.Pedido
import java.math.BigDecimal
import java.time.OffsetDateTime

data class PedidoCriadoMsg(
    val idPedido: Long,
    val numeroPedido: String,
    val dataHora: OffsetDateTime,
    var items: List<Item>,
    var valorTotal: BigDecimal
): Model {

    fun toModel() = Pedido(
        idPedido = idPedido,
        numeroPedido = numeroPedido,
        dataHora = dataHora,
        items = items,
        valorTotal = valorTotal
    )

    override fun valid(): PedidoCriadoMsg {
        require(numeroPedido.isNotBlank()) { "Número do pedido não pode ser vazio" }
        require(items.isNotEmpty()) { "Items do pedido não podem ser vazios" }
        return this
    }
}