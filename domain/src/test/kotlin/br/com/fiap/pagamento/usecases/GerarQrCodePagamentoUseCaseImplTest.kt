package br.com.fiap.pagamento.usecases

import br.com.fiap.pagamento.enums.PagamentoStatus
import br.com.fiap.pagamento.interfaces.gateways.GerarQrCodePagamentoGateway
import br.com.fiap.pagamento.models.Item
import br.com.fiap.pagamento.models.Pagamento
import br.com.fiap.pagamento.models.Pedido
import br.com.fiap.pagamento.models.Produto
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import java.math.BigDecimal
import java.time.OffsetDateTime
import java.util.*

@ExtendWith(MockKExtension::class)
class GerarQrCodePagamentoUseCaseImplTest {

    lateinit var target: GerarQrCodePagamentoUseCaseImpl

    @MockK
    lateinit var gerarQrCodePagamentoGateway: GerarQrCodePagamentoGateway

    @BeforeEach
    fun init() {
        target = GerarQrCodePagamentoUseCaseImpl(
            gerarQrCodePagamentoGateway
        )
    }

    @Test
    fun `deve gerar o qr code do pagamento com sucesso`() {
        //given
        val pedido = criarPedido()
        val pagamento = criarPagamento(pedido.idPedido)

        every { gerarQrCodePagamentoGateway.executar(any()) } returns pagamento

        //when
        val result = target.executar(pedido)

        //then
        assertEquals(pagamento.id, result.id)
        assertEquals(pagamento.idPedido, result.idPedido)
        assertEquals(pagamento.dataHora, result.dataHora)
        assertEquals(pagamento.qrCode, result.qrCode)
        assertEquals(pagamento.status, result.status)
        assertEquals(pagamento.valorTotal, result.valorTotal)

        verify(exactly = 1) { gerarQrCodePagamentoGateway.executar(pedido) }

    }

    private fun criarPedido() =
            Pedido(
                    idPedido = 123L,
                    numeroPedido = "123",
                    dataHora = OffsetDateTime.now(),
                    items = listOf(criarItem()),
                    valorTotal = BigDecimal.TEN
            )


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

    private fun criarPagamento(pedidoId: Long) =
            Pagamento(
                    id = UUID.randomUUID().toString(),
                    idPedido = pedidoId,
                    dataHora = OffsetDateTime.now().minusDays(1).toString(),
                    status = PagamentoStatus.APROVADO,
                    qrCode = Random().nextLong().toString(),
                    valorTotal = BigDecimal.TEN
            )
}