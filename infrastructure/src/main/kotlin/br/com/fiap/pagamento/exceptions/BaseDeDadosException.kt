package br.com.fiap.pagamento.exceptions

data class BaseDeDadosException(override val message: String): RuntimeException(message)