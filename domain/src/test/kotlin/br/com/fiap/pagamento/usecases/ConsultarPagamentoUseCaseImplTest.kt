package br.com.fiap.pagamento.usecases

import br.com.fiap.pagamento.enums.PagamentoStatus
import br.com.fiap.pagamento.exceptions.RecursoNaoEncontradoException
import br.com.fiap.pagamento.interfaces.PagamentoRepository
import br.com.fiap.pagamento.models.Pagamento
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal
import java.time.LocalDateTime
import java.util.*

@ExtendWith(MockKExtension::class)
class ConsultarPagamentoUseCaseImplTest {

    lateinit var target: ConsultarPagamentoUseCaseImpl

    @MockK
    lateinit var pagamentoRepository: PagamentoRepository

    @BeforeEach
    fun init() {
        target = ConsultarPagamentoUseCaseImpl(
            pagamentoRepository
        )
    }

    @Test
    fun `deve consultar o pagamento com sucesso`() {
        //given
        val pedidoId = 1L
        val pagamento = criarPagamento(pedidoId)

        every { pagamentoRepository.consultarPorPedido(any()) } returns pagamento

        //when
        val result = target.executar(pedidoId.toString())

        //then
        assertEquals(pagamento.id, result.id)
        assertEquals(pagamento.referenciaPedido, result.referenciaPedido)
        assertEquals(pagamento.dataHora, result.dataHora)
        assertEquals(pagamento.qrCode, result.qrCode)
        assertEquals(pagamento.status, result.status)
        assertEquals(pagamento.valorTotal, result.valorTotal)

        verify(exactly = 1) { pagamentoRepository.consultarPorPedido(pedidoId.toString()) }
    }

    @Test
    fun `deve lancar uma exception de recurso nao encontrado quando o pagamento nao existir`() {
        //given
        val pedidoId = 1L

        every { pagamentoRepository.consultarPorPedido(any()) } returns null

        //when-then
        val exception = assertThrows(RecursoNaoEncontradoException::class.java) {
            target.executar(pedidoId.toString())
        }

        //then
        assertEquals("Pagamento não encontrado", exception.message);

        verify(exactly = 1) { pagamentoRepository.consultarPorPedido(pedidoId.toString()) }
    }

    private fun criarPagamento(pedidoId: Long) =
        Pagamento(
            id = UUID.randomUUID().toString(),
            referenciaPedido = pedidoId.toString(),
            dataHora = LocalDateTime.now().minusDays(1),
            status = PagamentoStatus.APROVADO,
            qrCode = Random().nextLong().toString(),
            valorTotal = BigDecimal.TEN
        )

}