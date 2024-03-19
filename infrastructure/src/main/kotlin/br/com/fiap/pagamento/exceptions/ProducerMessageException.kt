package br.com.fiap.pagamento.exceptions

data class ProducerMessageException(override val message: String): RuntimeException(message)