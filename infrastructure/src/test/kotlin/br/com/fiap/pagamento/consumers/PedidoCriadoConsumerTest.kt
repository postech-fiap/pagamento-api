package br.com.fiap.pagamento.consumers

import br.com.fiap.pagamento.adapters.interfaces.PagamentoAdapter
import br.com.fiap.pagamento.enums.PagamentoStatus
import br.com.fiap.pagamento.models.Item
import br.com.fiap.pagamento.models.Pagamento
import br.com.fiap.pagamento.models.Produto
import br.com.fiap.pagamento.requests.PedidoCriadoMsg
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
import java.time.OffsetDateTime
import kotlin.random.Random

@ExtendWith(MockKExtension::class)
class PedidoCriadoConsumerTest {

    lateinit var target: PedidoCriadoConsumer

    @MockK
    lateinit var pagamentoAdapter: PagamentoAdapter

    @BeforeEach
    fun init() {
        target = PedidoCriadoConsumer(pagamentoAdapter)
    }

    @Test
    fun `deve criar um pagamento quando o pedido criado for recebido`() {
        //given
        val msg = criarPedidoCriadoMsg()

        every { pagamentoAdapter.criar(msg) } returns criarPagamento()

        //when
        target.consumidor(mensagem = msg)

        //then
        verify(exactly = 1) { pagamentoAdapter.criar(msg) }
    }

    @Test
    fun `deve propagar um erro quando o pagamento criado falhar`() {
        //given
        val msg = criarPedidoCriadoMsg()

        every { pagamentoAdapter.criar(msg) } throws RuntimeException("error")

        //when-then
        assertThatThrownBy { target.consumidor(mensagem = msg) }
            .isInstanceOf(Exception::class.java)

        //then
        verify(exactly = 1) { pagamentoAdapter.criar(msg) }
    }

    private fun criarPagamento() =
        Pagamento(
            id = Random.nextLong().toString(),
            idPedido = Random.nextLong(),
            dataHora = LocalDateTime.now().toString(),
            status = PagamentoStatus.APROVADO,
            qrCode = "qrcode",
            valorTotal = BigDecimal.TEN
        )

    private fun criarPedidoCriadoMsg() = PedidoCriadoMsg(
        idPedido = Random.nextLong(),
        numeroPedido = Random.nextLong().toString(),
        dataHora = OffsetDateTime.now(),
        items = listOf(
            Item(
                quantidade = 1,
                produto = Produto(
                    id = Random.nextLong(),
                    nome = "nome",
                    descricao = "descricao",
                    categoria = "categoria",
                    valor = BigDecimal.TEN
                ),
                valorPago = BigDecimal.TEN
            )
        ),
        valorTotal = BigDecimal.TEN
    )
}