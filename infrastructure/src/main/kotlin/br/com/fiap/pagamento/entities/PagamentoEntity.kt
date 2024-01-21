package br.com.fiap.pagamento.entities

import br.com.fiap.pagamento.enums.PagamentoStatus
import br.com.fiap.pagamento.models.Pagamento
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import org.springframework.data.mongodb.core.mapping.Field
import java.math.BigDecimal
import java.time.LocalDateTime

@Document(collection = "pagamento")
data class PagamentoEntity(
    @Id
    val id: String? = null,
    @Field("pedido_id")
    val pedidoId: Long,
    @Field("data_hora")
    val dataHora: LocalDateTime,
    val status: PagamentoStatus,
    @Field("qr_code")
    val qrCode: String,
    @Field("valor_total")
    val valorTotal: BigDecimal,
) {
    fun toModel() = Pagamento(id, pedidoId, dataHora, status, qrCode, valorTotal)

    companion object {
        fun fromModel(pagamento: Pagamento) = PagamentoEntity(
            id = pagamento.id,
            pedidoId = pagamento.pedidoId,
            dataHora = pagamento.dataHora,
            status = pagamento.status,
            qrCode = pagamento.qrCode,
            valorTotal = pagamento.valorTotal
        )
    }
}
