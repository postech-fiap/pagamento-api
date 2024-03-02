package br.com.fiap.pagamento.models

import br.com.fiap.pagamento.enums.PagamentoStatus
import java.math.BigDecimal

data class Pagamento(
    val id: String? = null,
    val idPedido: Long,
    val dataHora: String,
    val status: PagamentoStatus,
    val qrCode: String,
    val valorTotal: BigDecimal
)
