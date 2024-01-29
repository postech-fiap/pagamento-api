package br.com.fiap.pagamento.requests

import br.com.fiap.pagamento.interfaces.Model
import br.com.fiap.pagamento.models.mercadoPago.PagamentoCriado

data class PagamentoCriadoRequest(
    val action: String,
    val apiVersion: String,
    val data: DataRequest,
    val dateCreated: String,
    val id: Long,
    val liveMode: Boolean,
    val type: String,
    val userId: String
): Model {

    data class DataRequest(
        val id: String
    ) {

        fun toModel() = PagamentoCriado.Data(
            id = id
        )
    }

    fun toModel() = PagamentoCriado(
        action = action,
        apiVersion = apiVersion,
        data = data.toModel(),
        dateCreated = dateCreated,
        id = id,
        liveMode = liveMode,
        type = type,
        userId = userId
    )

    override fun valid(): PagamentoCriadoRequest {
        require("payment.created".equals(this.action, true)) { "Action inválida. Deve ser payment.created" }
        require("payment".equals(this.type, true)) { "Type inválido. Deve ser payment" }
        return this
    }
}