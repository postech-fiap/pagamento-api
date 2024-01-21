package br.com.fiap.pagamento.usecases

import br.com.fiap.pagamento.enums.PagamentoStatus
import br.com.fiap.pagamento.interfaces.PagamentoRepository
import br.com.fiap.pagamento.interfaces.usecases.ConsultarPagamentoUseCase
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
class AlterarStatusPagamentoUseCaseImplTest {

    lateinit var target: AlterarStatusPagamentoUseCaseImpl

    @MockK
    lateinit var consultarPagamentoUseCase: ConsultarPagamentoUseCase

    @MockK
    lateinit var pagamentoRepository: PagamentoRepository

    @BeforeEach
    fun init() {
        target = AlterarStatusPagamentoUseCaseImpl(
            consultarPagamentoUseCase,
            pagamentoRepository
        )
    }

    @Test
    fun `deveAlterarOStatusDoPagamentoComSucesso`() {
        //given
        val pedidoId = 1L
        val pagamento = criarPagamento(pedidoId)
        val novoStatus = PagamentoStatus.REPROVADO

        every { consultarPagamentoUseCase.executar(any()) } returns pagamento
        every { pagamentoRepository.alterarStatusPagamento(any(), any()) } returns
                pagamento.copy(status = novoStatus)

        //when
        val result = target.executar(pedidoId, novoStatus)

        //then
        assertEquals(pagamento.id, result.id)
        assertEquals(pagamento.pedidoId, result.pedidoId)
        assertEquals(pagamento.dataHora, result.dataHora)
        assertEquals(pagamento.qrCode, result.qrCode)
        assertEquals(novoStatus, result.status)
        assertEquals(pagamento.valorTotal, result.valorTotal)

        verify(exactly = 1) { consultarPagamentoUseCase.executar(pedidoId) }
        verify(exactly = 1) { pagamentoRepository.alterarStatusPagamento(pagamento, novoStatus) }
    }

    private fun criarPagamento(pedidoId: Long) =
            Pagamento(
                    id = UUID.randomUUID().toString(),
                    pedidoId = pedidoId,
                    dataHora = LocalDateTime.now().minusDays(1),
                    status = PagamentoStatus.APROVADO,
                    qrCode = Random().nextLong().toString(),
                    valorTotal = BigDecimal.TEN
            )

}