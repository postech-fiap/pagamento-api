package br.com.fiap.pagamento.exceptions

data class RecursoJaExisteException(override val message: String) : RuntimeException(message)
