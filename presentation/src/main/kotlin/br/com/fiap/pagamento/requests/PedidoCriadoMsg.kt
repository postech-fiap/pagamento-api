package br.com.fiap.pagamento.requests

import br.com.fiap.pagamento.interfaces.Model
import br.com.fiap.pagamento.models.Item
import br.com.fiap.pagamento.models.Pedido
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable
import java.math.BigDecimal
import java.time.OffsetDateTime

data class PedidoCriadoMsg(
    @JsonProperty("id_pedido")
    val idPedido: Long? = null,
    @JsonProperty("numero_pedido")
    val numeroPedido: String? = null,
    @JsonProperty("data_hora")
    val dataHora: OffsetDateTime? = null,
    @JsonProperty("items")
    var items: List<Item>? = null,
    @JsonProperty("valor_total")
    var valorTotal: BigDecimal? = null
): Model, Serializable {

    fun toModel() = Pedido(
        idPedido = idPedido!!,
        numeroPedido = numeroPedido!!,
        dataHora = dataHora!!,
        items = items!!,
        valorTotal = valorTotal!!
    )

    override fun valid(): PedidoCriadoMsg {
        require(numeroPedido!!.isNotBlank()) { "Número do pedido não pode ser vazio" }
        require(items!!.isNotEmpty()) { "Items do pedido não podem ser vazios" }
        return this
    }
}