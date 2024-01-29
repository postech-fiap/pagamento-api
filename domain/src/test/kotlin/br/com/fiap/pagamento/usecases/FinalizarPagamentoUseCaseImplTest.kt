package br.com.fiap.pagamento.usecases

import br.com.fiap.pagamento.enums.PagamentoStatus
import br.com.fiap.pagamento.interfaces.gateways.AtualizarPedidoGateway
import br.com.fiap.pagamento.interfaces.gateways.BuscarPagamentoPorIdGateway
import br.com.fiap.pagamento.interfaces.usecases.AlterarStatusPagamentoUseCase
import br.com.fiap.pagamento.models.Pagamento
import br.com.fiap.pagamento.models.mercadoPago.MerchantOrders
import br.com.fiap.pagamento.models.mercadoPago.PagamentoCriado
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
import java.time.OffsetDateTime
import java.util.*

@ExtendWith(MockKExtension::class)
class FinalizarPagamentoUseCaseImplTest {

    lateinit var target: FinalizarPagamentoUseCaseImpl

    @MockK
    lateinit var buscarPagamentoPorIdGateway: BuscarPagamentoPorIdGateway

    @MockK
    lateinit var alterarStatusPagamentoUseCase: AlterarStatusPagamentoUseCase

    @MockK
    lateinit var atualizarPedidoGateway: AtualizarPedidoGateway

    @BeforeEach
    fun init() {
        target = FinalizarPagamentoUseCaseImpl(
            buscarPagamentoPorIdGateway,
            alterarStatusPagamentoUseCase,
            atualizarPedidoGateway
        )
    }

    @Test
    fun `deve atualizar o pagamento como FINALIZADO quando o pagamento for aprovado`() {
        //given
        val referenciaPedido = "1"
        val pedidoMercadoPago = criarPedidoMercadoPago(referenciaPedido, "approved")
        val notificacaoPagamentoCriado = criarPagamentoNotificado()
        val pagamento = criarPagamento(referenciaPedido, notificacaoPagamentoCriado.data.id)

        every { buscarPagamentoPorIdGateway.executar(any()) } returns pedidoMercadoPago
        every { alterarStatusPagamentoUseCase.executar(any(), any()) } returns pagamento
        every { atualizarPedidoGateway.executar(any(), any(), any()) } returns Unit

        //when
        val result = target.executar(notificacaoPagamentoCriado)

        //then
        assertEquals(notificacaoPagamentoCriado.data.id, result.id)
        assertEquals(pedidoMercadoPago.elements!![0].externalReference, result.referenciaPedido)
        assertEquals(pagamento.dataHora, result.dataHora)
        assertEquals(pagamento.qrCode, result.qrCode)
        assertEquals(PagamentoStatus.APROVADO, result.status)
        assertEquals(pagamento.valorTotal, result.valorTotal)

        verify(exactly = 1) { buscarPagamentoPorIdGateway.executar(notificacaoPagamentoCriado.data.id) }
        verify(exactly = 1) { alterarStatusPagamentoUseCase.executar(referenciaPedido, PagamentoStatus.APROVADO) }
        verify(exactly = 1) { atualizarPedidoGateway.executar(referenciaPedido, pagamento.id!!, PagamentoStatus.APROVADO) }
    }

    @Test
    fun `deve atualizar o pagamento como REPROVADO quando o pagamento nao for aprovado`() {
        //given
        val referenciaPedido = "1"
        val pedidoMercadoPago = criarPedidoMercadoPago(referenciaPedido, "invalid")
        val notificacaoPagamentoCriado = criarPagamentoNotificado()
        val pagamento = criarPagamento(referenciaPedido, notificacaoPagamentoCriado.data.id)
                .copy(status = PagamentoStatus.REPROVADO)

        every { buscarPagamentoPorIdGateway.executar(any()) } returns pedidoMercadoPago
        every { alterarStatusPagamentoUseCase.executar(any(), any()) } returns pagamento
        every { atualizarPedidoGateway.executar(any(), any(), any()) } returns Unit

        //when
        val result = target.executar(notificacaoPagamentoCriado)

        //then
        assertEquals(notificacaoPagamentoCriado.data.id, result.id)
        assertEquals(pedidoMercadoPago.elements!![0].externalReference, result.referenciaPedido.toString())
        assertEquals(pagamento.dataHora, result.dataHora)
        assertEquals(pagamento.qrCode, result.qrCode)
        assertEquals(PagamentoStatus.REPROVADO, result.status)
        assertEquals(pagamento.valorTotal, result.valorTotal)

        verify(exactly = 1) { buscarPagamentoPorIdGateway.executar(notificacaoPagamentoCriado.data.id) }
        verify(exactly = 1) { alterarStatusPagamentoUseCase.executar(referenciaPedido.toString(), PagamentoStatus.REPROVADO) }
        verify(exactly = 1) { atualizarPedidoGateway.executar(referenciaPedido, pagamento.id!!, PagamentoStatus.REPROVADO) }
    }

    private fun criarPagamento(referenciaPedido: String, pagamentoId: String) =
            Pagamento(
                    id = pagamentoId,
                    referenciaPedido = referenciaPedido.toString(),
                    dataHora = OffsetDateTime.now().minusDays(1).toString(),
                    status = PagamentoStatus.APROVADO,
                    qrCode = Random().nextLong().toString(),
                    valorTotal = BigDecimal.TEN
            )

    private fun criarPedidoMercadoPago(referenciaPedido: String, pagamentoStatus: String) =
            MerchantOrders(
                    elements = listOf(
                            MerchantOrders.Elements(
                                    id = Random().nextLong(),
                                    status = "Status",
                                    externalReference = referenciaPedido,
                                    payments = listOf(MerchantOrders.Elements.Payment(
                                            id = Random().nextLong(),
                                            transactionAmount = BigDecimal.TEN,
                                            totalPaidAmount = BigDecimal.TEN,
                                            status = pagamentoStatus,
                                            statusDetail = "status detail",
                                            dateApproved = LocalDateTime.now().minusDays(1).toString(),
                                            dateCreated = LocalDateTime.now().minusDays(1).toString(),
                                            lastModified = LocalDateTime.now().minusDays(1).toString(),
                                            amountRefunded = BigDecimal.ZERO
                                    ))
                            )
                    )
            )

    private fun criarPagamentoNotificado() =
            PagamentoCriado(
                    action = "action",
                    apiVersion = "api_version",
                    data = PagamentoCriado.Data(id = Random().nextLong().toString()),
                    dateCreated = LocalDateTime.now().minusDays(1).toString(),
                    id = Random().nextLong(),
                    liveMode = false,
                    type = "payment",
                    userId = Random().nextLong().toString()
            )

}