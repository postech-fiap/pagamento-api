package br.com.fiap.pagamento.gateways

import br.com.fiap.pagamento.enums.PagamentoStatus
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import kotlin.random.Random

private const val PEDIDO_API_ENDPOINT = "pedidoApiEndpoint"

@ExtendWith(MockKExtension::class)
class AtualizarPedidoHttpGatewayImplTest {

    lateinit var target: AtualizarPedidoHttpGatewayImpl

    @MockK
    lateinit var restTemplate: RestTemplate

    @BeforeEach
    fun init() {
        target = AtualizarPedidoHttpGatewayImpl(
            restTemplate,
            PEDIDO_API_ENDPOINT
        )
    }

    @Test
    fun `deve atualizar o pedido com sucesso`() {
        //given
        val random = Random.nextLong()
        val idPedido = random.toString()
        val idPagamento = random.toString()
        val statusPagamento = PagamentoStatus.APROVADO

        every {
            restTemplate.exchange(
                eq(PEDIDO_API_ENDPOINT),
                eq(HttpMethod.PUT),
                any(),
                eq(String::class.java)
            )
        } returns ResponseEntity("123", HttpStatus.OK)

        //when
        val pedidoResponse = target.executar(idPedido, idPagamento, statusPagamento)

        //then
        assertNotNull(pedidoResponse)

        verify(exactly = 1) {
            restTemplate.exchange(
                eq(PEDIDO_API_ENDPOINT),
                eq(HttpMethod.PUT),
                any(),
                eq(String::class.java)
            )
        }
    }

    @Test
    fun `deve lancar um erro quando a integracao de atualizar o pedido falhar`() {
        //given
        val random = Random.nextLong()
        val idPedido = random.toString()
        val idPagamento = random.toString()
        val statusPagamento = PagamentoStatus.APROVADO

        every {
            restTemplate.exchange(
                eq(PEDIDO_API_ENDPOINT),
                eq(HttpMethod.PUT),
                any(),
                eq(String::class.java)
            )
        } throws Exception("Error")

        val errorMessageExpected = "Erro de integração para atualizar o pedido. Detalhes: Error - null"

        //when-then
        val exception = assertThrows(RuntimeException::class.java) {
            target.executar(idPedido, idPagamento, statusPagamento)
        }

        //then
        assertEquals(errorMessageExpected, exception.message)

        verify(exactly = 1) {
            restTemplate.exchange(
                eq(PEDIDO_API_ENDPOINT),
                eq(HttpMethod.PUT),
                any(),
                eq(String::class.java)
            )
        }

    }

    @Test
    fun `deve lancar um erro quando o retorno da integracao for diferente de 200`() {
        //given
        val random = Random.nextLong()
        val idPedido = random.toString()
        val idPagamento = random.toString()
        val statusPagamento = PagamentoStatus.APROVADO

        every {
            restTemplate.exchange(
                eq(PEDIDO_API_ENDPOINT),
                eq(HttpMethod.PUT),
                any(),
                eq(String::class.java)
            )
        } returns ResponseEntity(
           "123",
            HttpStatus.CONFLICT
        )

        val errorMessageExpected = "Erro de integração para atualizar o pedido. Detalhes: [status_code: 409 CONFLICT"

        //when-then
        val exception = assertThrows(RuntimeException::class.java) {
            target.executar(idPedido, idPagamento, statusPagamento)
        }

        //then
        assertEquals(errorMessageExpected, exception.message)

        verify(exactly = 1) {
            restTemplate.exchange(
                eq(PEDIDO_API_ENDPOINT),
                eq(HttpMethod.PUT),
                any(),
                eq(String::class.java)
            )
        }
    }
}