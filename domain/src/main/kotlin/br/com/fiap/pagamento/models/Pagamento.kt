package br.com.fiap.pagamento.models

import br.com.fiap.pagamento.enums.PagamentoStatus
import java.math.BigDecimal
import java.time.LocalDateTime

data class Pagamento(
    val id: String? = null,
    val referenciaPedido: String,
    val dataHora: LocalDateTime,
    val status: PagamentoStatus,
    val qrCode: String,
    val valorTotal: BigDecimal
)
