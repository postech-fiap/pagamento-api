package br.com.fiap.pagamento.models

import java.math.BigDecimal

data class Item(
    val quantidade: Long,
    val produto: Produto,
    val valorPago: BigDecimal
)
