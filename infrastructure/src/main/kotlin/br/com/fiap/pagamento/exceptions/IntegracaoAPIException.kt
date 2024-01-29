package br.com.fiap.pagamento.exceptions

data class IntegracaoAPIException(override val message: String): RuntimeException(message)