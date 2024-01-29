package br.com.fiap.pagamento.models.mercadoPago

data class PagamentoCriado(
    val action: String,
    val apiVersion: String,
    val data: Data,
    val dateCreated: String,
    val id: Long,
    val liveMode: Boolean,
    val type: String,
    val userId: String
) {
    data class Data(val id: String)
}
