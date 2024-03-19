package br.com.fiap.pagamento.gateways

import br.com.fiap.pagamento.dtos.MercadoPagoResponseOrdemDto
import br.com.fiap.pagamento.enums.PagamentoStatus
import br.com.fiap.pagamento.gateways.http.GerarQrCodePagamentoHttpGatewayImpl
import br.com.fiap.pagamento.models.Item
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
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestTemplate
import java.math.BigDecimal
import kotlin.random.Random

private const val MERCADO_PAGO_QR_CODE_ENDPOINT = "mercadoPagoApiGenerateQrcodeEndpoint"
private const val MERCADO_PAGO_TOKEN = "mercadoPagoToken"
private const val MERCADO_PAGO_WEBHOOK_URL = "mercadoPagoWebhookUrl"

@ExtendWith(MockKExtension::class)
class GerarQrCodePagamentoHttpGatewayImplTest {

    lateinit var target: GerarQrCodePagamentoHttpGatewayImpl

    @MockK
    lateinit var restTemplate: RestTemplate

    @BeforeEach
    fun init() {
        target = GerarQrCodePagamentoHttpGatewayImpl(
                restTemplate,
                MERCADO_PAGO_QR_CODE_ENDPOINT,
                MERCADO_PAGO_TOKEN,
                MERCADO_PAGO_WEBHOOK_URL
        )
    }

    @Test
    fun `deve gerar o pagamento pendente com qrcode`() {
        //given
        val pedido = Pedido(
                idPedido = 1L,
                numeroPedido = "1",
                valorTotal = Random.nextLong().toBigDecimal(),
                items = listOf(
                        Item(
                                produto = Produto(
                                        id = 1L,
                                        nome = "Nome",
                                        descricao = "Description",
                                        categoria = "BEBIDA",
                                        valor = BigDecimal.valueOf(1.0)
                                ),
                                quantidade = 1,
                                valorPago = Random.nextLong().toBigDecimal()
                        ),
                ),
        )

        every {
            restTemplate.postForEntity(
                    eq(MERCADO_PAGO_QR_CODE_ENDPOINT),
                    any(),
                    eq(MercadoPagoResponseOrdemDto::class.java)
            )
        } returns ResponseEntity(
                MercadoPagoResponseOrdemDto(Random.nextLong().toString(), Random.nextLong().toString()),
                HttpStatus.CREATED
        )

        //when
        val pagamentoComQrCode = target.executar(pedido)

        //then
        assertNotNull(pagamentoComQrCode.qrCode)
        assertNotNull(pagamentoComQrCode.valorTotal)
        assertNotNull(pagamentoComQrCode.dataHora)
        assertEquals(PagamentoStatus.PENDENTE, pagamentoComQrCode.status)

        verify(exactly = 1) {
            restTemplate.postForEntity(
                    eq(MERCADO_PAGO_QR_CODE_ENDPOINT),
                    any(),
                    eq(MercadoPagoResponseOrdemDto::class.java)
            )
        }
    }

    @Test
    fun `deve lancar um erro quando a integracao de gerar o pagamento falhar`() {
        //given
        val pedido = Pedido(
                idPedido = 1L,
                numeroPedido = "1",
                valorTotal = Random.nextLong().toBigDecimal(),
                items = listOf(
                        Item(
                                produto = Produto(
                                        id = 1L,
                                        nome = "Nome",
                                        descricao = "Description",
                                        categoria = "BEBIDA",
                                        valor = BigDecimal.valueOf(1.0)
                                ),
                                quantidade = 1,
                                valorPago = Random.nextLong().toBigDecimal()
                        ),
                ),
        )

        every {
            restTemplate.postForEntity(
                    eq(MERCADO_PAGO_QR_CODE_ENDPOINT),
                    any(),
                    eq(MercadoPagoResponseOrdemDto::class.java)
            )
        } throws Exception("Error")

        val errorMessageExpected = "Erro de integração para gerar o pagamento. Detalhes: Error - null"

        //when-then
        val exception = assertThrows(RuntimeException::class.java) {
            target.executar(pedido)
        }

        //then
        assertEquals(errorMessageExpected, exception.message)

        verify(exactly = 1) {
            restTemplate.postForEntity(
                    eq(MERCADO_PAGO_QR_CODE_ENDPOINT),
                    any(),
                    eq(MercadoPagoResponseOrdemDto::class.java)
            )
        }

    }

    @Test
    fun `deve lancar um erro quando o retorno da integracao for diferente de 201`() {
        //given
        val pedido = Pedido(
                idPedido = 1L,
                numeroPedido = "1",
                valorTotal = Random.nextLong().toBigDecimal(),
                items = listOf(
                        Item(
                                produto = Produto(
                                        id = 1L,
                                        nome = "Nome",
                                        descricao = "Description",
                                        categoria = "BEBIDA",
                                        valor = BigDecimal.valueOf(1.0)
                                ),
                                quantidade = 1,
                                valorPago = Random.nextLong().toBigDecimal()
                        ),
                ),
        )

        every {
            restTemplate.postForEntity(
                    eq(MERCADO_PAGO_QR_CODE_ENDPOINT),
                    any(),
                    eq(MercadoPagoResponseOrdemDto::class.java)
            )
        } returns ResponseEntity(
                MercadoPagoResponseOrdemDto(Random.nextLong().toString(), Random.nextLong().toString()),
                HttpStatus.CONFLICT
        )

        val errorMessageExpected = "Erro de integração para gerar o pagamento. Detalhes: [status_code: 409 CONFLICT"

        //when-then
        val exception = assertThrows(RuntimeException::class.java) {
            target.executar(pedido)
        }

        //then
        assertEquals(errorMessageExpected, exception.message)

        verify(exactly = 1) {
            restTemplate.postForEntity(
                    eq(MERCADO_PAGO_QR_CODE_ENDPOINT),
                    any(),
                    eq(MercadoPagoResponseOrdemDto::class.java)
            )
        }
    }
}