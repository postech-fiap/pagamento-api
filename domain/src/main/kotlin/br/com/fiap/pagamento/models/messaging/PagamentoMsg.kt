package br.com.fiap.pagamento.models.messaging

import br.com.fiap.pagamento.enums.PagamentoStatus
import com.fasterxml.jackson.annotation.JsonProperty
import java.io.Serializable

data class PagamentoMsg(
        @JsonProperty("id_pedido")
        val idPedido: Long,
        @JsonProperty("id_pagamento")
        val idPagamento: String,
        @JsonProperty("status_pagamento")
        val statusPagamento: PagamentoStatus
): Serializable