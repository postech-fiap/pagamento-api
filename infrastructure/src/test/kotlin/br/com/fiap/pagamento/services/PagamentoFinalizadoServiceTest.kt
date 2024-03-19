package br.com.fiap.pagamento.services

import br.com.fiap.pagamento.enums.PagamentoStatus
import br.com.fiap.pagamento.interfaces.gateways.EnviarMensagemPagamentoGateway
import br.com.fiap.pagamento.interfaces.usecases.AlterarStatusPagamentoUseCase
import br.com.fiap.pagamento.models.Pagamento
import br.com.fiap.pagamento.models.messaging.PagamentoMsg
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.assertj.core.api.Assertions.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal
import java.time.LocalDateTime
import kotlin.random.Random

@ExtendWith(MockKExtension::class)
class PagamentoFinalizadoServiceTest {

    lateinit var target: PagamentoFinalizadoService

    @MockK
    lateinit var alterarStatusPagamentoUseCase: AlterarStatusPagamentoUseCase

    @MockK
    lateinit var enviarMensagemPagamentoGateway: EnviarMensagemPagamentoGateway

    @BeforeEach
    fun init() {
        target = PagamentoFinalizadoService(alterarStatusPagamentoUseCase, enviarMensagemPagamentoGateway)
    }

    @Test
    fun `deve atualizar e postar a mensagem de pagamento com sucesso`() {
        //given
        val idPedido = 1L
        val pagamento = criarPagamento(idPedido)
        val pagamentoMsg = criarPagamentoMsg(idPedido, pagamento)

        every { alterarStatusPagamentoUseCase.executar(idPedido, PagamentoStatus.APROVADO) } returns pagamento
        every { enviarMensagemPagamentoGateway.executar("pagamento-finalizado", pagamentoMsg) } returns Unit

        //when
        val result = target.atualizaEPostaMensagem(idPedido, PagamentoStatus.APROVADO)

        //then
        assertNotNull(result.id)
        assertNotNull(result.qrCode)
        assertNotNull(result.status)
        assertNotNull(result.valorTotal)
        assertNotNull(result.idPedido)
        assertNotNull(result.dataHora)

        verify(exactly = 1) { alterarStatusPagamentoUseCase.executar(idPedido, PagamentoStatus.APROVADO) }
        verify(exactly = 1) { enviarMensagemPagamentoGateway.executar("pagamento-finalizado", pagamentoMsg) }
    }

    @Test
    fun `deve lancar um erro quando alguma integracao falhar`() {
        //given
        val idPedido = 1L
        val pagamento = criarPagamento(idPedido)
        val pagamentoMsg = criarPagamentoMsg(idPedido, pagamento)

        every { alterarStatusPagamentoUseCase.executar(idPedido, PagamentoStatus.APROVADO) } returns pagamento
        every { enviarMensagemPagamentoGateway.executar("pagamento-finalizado", pagamentoMsg) } throws RuntimeException("Error")

        //when-then
        assertThatThrownBy { target.atualizaEPostaMensagem(idPedido, PagamentoStatus.APROVADO) }
            .isInstanceOf(Exception::class.java)

        verify(exactly = 1) { alterarStatusPagamentoUseCase.executar(idPedido, PagamentoStatus.APROVADO) }
        verify(exactly = 1) { enviarMensagemPagamentoGateway.executar("pagamento-finalizado", pagamentoMsg) }
    }

    private fun criarPagamentoMsg(
        idPedido: Long,
        pagamento: Pagamento
    ) = PagamentoMsg(idPedido = idPedido, idPagamento = pagamento.id!!, statusPagamento = pagamento.status)

    private fun criarPagamento(idPedido: Long) =
        Pagamento(
            id = Random.nextLong().toString(),
            idPedido = idPedido,
            dataHora = LocalDateTime.now().toString(),
            status = PagamentoStatus.APROVADO,
            qrCode = "qrcode",
            valorTotal = BigDecimal.TEN
        )
}