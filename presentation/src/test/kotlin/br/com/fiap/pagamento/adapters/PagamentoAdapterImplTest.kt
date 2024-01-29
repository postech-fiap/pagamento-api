package br.com.fiap.pagamento.adapters

import br.com.fiap.pagamento.enums.PagamentoStatus
import br.com.fiap.pagamento.exceptions.RecursoJaExisteException
import br.com.fiap.pagamento.exceptions.RecursoNaoEncontradoException
import br.com.fiap.pagamento.interfaces.PagamentoRepository
import br.com.fiap.pagamento.interfaces.usecases.ConsultarPagamentoUseCase
import br.com.fiap.pagamento.interfaces.usecases.FinalizarPagamentoUseCase
import br.com.fiap.pagamento.interfaces.usecases.GerarQrCodePagamentoUseCase
import br.com.fiap.pagamento.models.Item
import br.com.fiap.pagamento.models.Pagamento
import br.com.fiap.pagamento.models.Produto
import br.com.fiap.pagamento.requests.PagamentoCriadoRequest
import br.com.fiap.pagamento.requests.PedidoCriadoRequest
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
class PagamentoAdapterImplTest {

    lateinit var target: PagamentoAdapterImpl

    @MockK
    lateinit var gerarQrCodePagamentoUseCase: GerarQrCodePagamentoUseCase

    @MockK
    lateinit var pagamentoRepository: PagamentoRepository

    @MockK
    lateinit var consultarPagamentoUseCase: ConsultarPagamentoUseCase

    @MockK
    lateinit var finalizarPagamentoUseCase: FinalizarPagamentoUseCase

    @BeforeEach
    fun init() {
        target = PagamentoAdapterImpl(
            gerarQrCodePagamentoUseCase,
            pagamentoRepository,
            consultarPagamentoUseCase,
            finalizarPagamentoUseCase
        )
    }

    @Test
    fun `deve criar o pagamento com sucesso quando nao existir`() {
        //given
        val pedidoId = "1"
        val pagamento = criarPagamento(pedidoId)
        val pedidoCriadoRequest = PedidoCriadoRequest(
            referenciaPedido = pedidoId,
            numeroPedido = "123",
            dataHora = OffsetDateTime.now(),
            items = listOf(criarItem(), criarItem()),
            valorTotal = BigDecimal.TEN
        )

        every { consultarPagamentoUseCase.executar(any()) } throws RecursoNaoEncontradoException("message")
        every { gerarQrCodePagamentoUseCase.executar(any()) } returns pagamento
        every { pagamentoRepository.salvar(any()) } returns pagamento

        //when
        val result = target.criar(pedidoCriadoRequest)

        //then
        assertEquals(pagamento.id, result.id)
        assertEquals(pagamento.referenciaPedido, result.referenciaPedido)
        assertEquals(pagamento.dataHora, result.dataHora)
        assertEquals(pagamento.qrCode, result.qrCode)
        assertEquals(pagamento.status, result.status)
        assertEquals(pagamento.valorTotal, result.valorTotal)

        verify(exactly = 1) { consultarPagamentoUseCase.executar(pedidoId) }
        verify(exactly = 1) { gerarQrCodePagamentoUseCase.executar(any()) }
        verify(exactly = 1) { pagamentoRepository.salvar(pagamento) }
    }

    @Test
    fun `deve lancar uma exception ao criar o pagamento quando ele ja existir`() {
        //given
        val pedidoId = "1"
        val pagamento = criarPagamento(pedidoId)
        val pedidoCriadoRequest = PedidoCriadoRequest(
            referenciaPedido = pedidoId,
            numeroPedido = "123",
            dataHora = OffsetDateTime.now(),
            items = listOf(criarItem(), criarItem()),
            valorTotal = BigDecimal.TEN
        )

        every { consultarPagamentoUseCase.executar(any()) } returns pagamento

        //when-then
        val exception = assertThrows(RecursoJaExisteException::class.java) {
            target.criar(pedidoCriadoRequest)
        }

        //then
        assertEquals("Pagamento já existe para o pedido com referencia externa: $pedidoId", exception.message);

        verify(exactly = 1) { consultarPagamentoUseCase.executar(pedidoId) }
        verify(exactly = 0) { gerarQrCodePagamentoUseCase.executar(any()) }
        verify(exactly = 0) { pagamentoRepository.salvar(pagamento) }
    }

    @Test
    fun `deve finalizar o pagamento com sucesso`() {
        //given
        val pagamentoId = 1L
        val pedidoId = "1"
        val pagamento = criarPagamento(pedidoId)

        val pagamentoCriadoRequest = pagamentoCriadoRequest(pagamentoId)

        every { finalizarPagamentoUseCase.executar(any()) } returns pagamento

        //when
        val result = target.finalizar(pagamentoCriadoRequest)

        //then
        assertEquals(pagamento.id, result.id)
        assertEquals(pagamento.referenciaPedido, result.referenciaPedido)
        assertEquals(pagamento.dataHora, result.dataHora)
        assertEquals(pagamento.qrCode, result.qrCode)
        assertEquals(pagamento.status, result.status)
        assertEquals(pagamento.valorTotal, result.valorTotal)

        verify(exactly = 1) { finalizarPagamentoUseCase.executar(pagamentoCriadoRequest.toModel()) }
    }

    private fun pagamentoCriadoRequest(pagamentoId: Long): PagamentoCriadoRequest {
        return PagamentoCriadoRequest(
            id = Random().nextLong(),
            action = "action",
            apiVersion = "apiVersion",
            data = PagamentoCriadoRequest.DataRequest(id = pagamentoId.toString()),
            dateCreated = LocalDateTime.now().toString(),
            liveMode = true,
            type = "type",
            userId = "userId"
        )
    }

    private fun criarItem() = Item(
        quantidade = 1,
        valorPago = BigDecimal(10),
        produto = criarProduto()
    )

    private fun criarProduto() = Produto(
        id = 1,
        nome = "Produto 1",
        descricao = "descricao",
        categoria = "BEBIDA",
        valor = BigDecimal(10)
    )

    private fun criarPagamento(pedidoId: String) =
        Pagamento(
            id = UUID.randomUUID().toString(),
            referenciaPedido = pedidoId,
            dataHora = LocalDateTime.now().minusDays(1).toString(),
            status = PagamentoStatus.APROVADO,
            qrCode = Random().nextLong().toString(),
            valorTotal = BigDecimal.TEN
        )

}