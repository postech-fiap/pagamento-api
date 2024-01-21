package br.com.fiap.pagamento.models

import java.math.BigDecimal

data class Produto(
    val id: Long,
    val nome: String,
    val descricao: String,
    val categoria: String,
    val valor: BigDecimal = BigDecimal.ZERO,
)
