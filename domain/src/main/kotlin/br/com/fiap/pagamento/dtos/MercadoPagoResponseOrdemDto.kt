package br.com.fiap.pagamento.dtos

import br.com.fiap.pagamento.enums.PagamentoStatus
import br.com.fiap.pagamento.models.Pagamento
import br.com.fiap.pagamento.models.Pedido
import java.time.OffsetDateTime

data class MercadoPagoResponseOrdemDto(
    val inStoreOrderId: String,
    val qrData: String
) {
    fun toModel(pedido: Pedido) = Pagamento(
        dataHora = OffsetDateTime.now().toString(),
        status = PagamentoStatus.PENDENTE,
        qrCode = qrData,
        valorTotal = pedido.valorTotal,
        idPedido = pedido.idPedido
    )
}