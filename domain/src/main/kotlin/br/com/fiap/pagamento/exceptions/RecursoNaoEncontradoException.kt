package br.com.fiap.pagamento.exceptions

data class RecursoNaoEncontradoException(override val message: String) : RuntimeException(message)