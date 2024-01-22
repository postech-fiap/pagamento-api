package br.com.fiap.pagamento.models

import java.math.BigDecimal
import java.time.OffsetDateTime

data class Pedido(
    val referenciaPedido: String,
    val numeroPedido: String,
    val dataHora: OffsetDateTime = OffsetDateTime.now(),
    var items: List<Item> = listOf(),
    var valorTotal: BigDecimal
)
