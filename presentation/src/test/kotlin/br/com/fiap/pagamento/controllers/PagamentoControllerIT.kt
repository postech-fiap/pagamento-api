package br.com.fiap.pagamento.controllers

import br.com.fiap.pagamento.adapters.interfaces.PagamentoAdapter
import br.com.fiap.pagamento.config.IntegrationTest
import br.com.fiap.pagamento.enums.PagamentoStatus
import br.com.fiap.pagamento.exceptions.BaseDeDadosException
import br.com.fiap.pagamento.exceptions.RecursoJaExisteException
import br.com.fiap.pagamento.models.Item
import br.com.fiap.pagamento.models.Pagamento
import br.com.fiap.pagamento.models.Produto
import br.com.fiap.pagamento.requests.PagamentoCriadoRequest
import br.com.fiap.pagamento.requests.PedidoCriadoMsg
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.BeanInstantiationException
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import java.math.BigDecimal
import java.time.LocalDateTime
import java.time.OffsetDateTime
import java.util.*

class PagamentoControllerIT : IntegrationTest() {

    @MockkBean
    lateinit var pagamentoAdapter: PagamentoAdapter

    @Test
    fun `deve retornar 201 created ao criar com sucesso`() {
        //given
        val pedidoCriadoRequest = criarPedidoRequest()
        val pagamento = criarPagamento(1L)

        every { pagamentoAdapter.criar(any()) } returns pagamento

        val json = objectMapper.writeValueAsString(pedidoCriadoRequest)

        //when-then
        mockMvc.post("/v1/pagamentos/criar") {
            contentType = MediaType.APPLICATION_JSON
            content = json
            accept = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isCreated() }
                jsonPath("$.id").value(pagamento.id)
                jsonPath("$.referenciaPedido").value(pagamento.idPedido)
                jsonPath("$.dataHora").value(pagamento.dataHora)
                jsonPath("$.status").value(pagamento.status)
                jsonPath("$.qrCode").value(pagamento.qrCode)
                jsonPath("$.valorTotal").value(pagamento.valorTotal)
            }
    }

    @Test
    fun `deve retornar 400 bad request ao criar quando a request tem numeroPedido invalido`() {
        //given
        val pedidoCriadoRequest = criarPedidoRequest().copy(numeroPedido = "")
        val pagamento = criarPagamento(pedidoCriadoRequest.idPedido)

        every { pagamentoAdapter.criar(any()) } returns pagamento

        val json = objectMapper.writeValueAsString(pedidoCriadoRequest)

        //when-then
        mockMvc.post("/v1/pagamentos/criar") {
            contentType = MediaType.APPLICATION_JSON
            content = json
            accept = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isBadRequest() }
            }
    }

    @Test
    fun `deve retornar 400 bad request ao criar quando a request tem itens invalidos`() {
        //given
        val pedidoCriadoRequest = criarPedidoRequest().copy(items = emptyList())
        val pagamento = criarPagamento(pedidoCriadoRequest.idPedido)

        every { pagamentoAdapter.criar(any()) } returns pagamento

        val json = objectMapper.writeValueAsString(pedidoCriadoRequest)

        //when-then
        mockMvc.post("/v1/pagamentos/criar") {
            contentType = MediaType.APPLICATION_JSON
            content = json
            accept = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isBadRequest() }
            }
    }

    @Test
    fun `deve retornar 409 conflict ao criar quando ja existe`() {
        //given
        val pedidoCriadoRequest = criarPedidoRequest()

        every { pagamentoAdapter.criar(any()) } throws RecursoJaExisteException("error")

        val json = objectMapper.writeValueAsString(pedidoCriadoRequest)

        //when-then
        mockMvc.post("/v1/pagamentos/criar") {
            contentType = MediaType.APPLICATION_JSON
            content = json
            accept = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isConflict() }
            }
    }

    @Test
    fun `deve retornar 500 internal server error ao criar com erro nao mapeado`() {
        //given
        val pedidoCriadoRequest = criarPedidoRequest()

        every { pagamentoAdapter.criar(any()) } throws RuntimeException("error")

        val json = objectMapper.writeValueAsString(pedidoCriadoRequest)

        //when-then
        mockMvc.post("/v1/pagamentos/criar") {
            contentType = MediaType.APPLICATION_JSON
            content = json
            accept = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isInternalServerError() }
            }
    }

    @Test
    fun `deve retornar 500 internal server error ao criar com erro de base de dados`() {
        //given
        val pedidoCriadoRequest = criarPedidoRequest()

        every { pagamentoAdapter.criar(any()) } throws BaseDeDadosException("error")

        val json = objectMapper.writeValueAsString(pedidoCriadoRequest)

        //when-then
        mockMvc.post("/v1/pagamentos/criar") {
            contentType = MediaType.APPLICATION_JSON
            content = json
            accept = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isInternalServerError() }
            }
    }

    @Test
    fun `deve retornar 400 bad request error ao criar com erro illegal argument`() {
        //given
        val pedidoCriadoRequest = criarPedidoRequest()

        every { pagamentoAdapter.criar(any()) } throws IllegalArgumentException("error")

        val json = objectMapper.writeValueAsString(pedidoCriadoRequest)

        //when-then
        mockMvc.post("/v1/pagamentos/criar") {
            contentType = MediaType.APPLICATION_JSON
            content = json
            accept = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isBadRequest() }
            }
    }

    @Test
    fun `deve retornar 400 bad request error ao criar com erro de bean`() {
        //given
        val pedidoCriadoRequest = criarPedidoRequest()

        every { pagamentoAdapter.criar(any()) } throws BeanInstantiationException(RuntimeException::class.java, "")

        val json = objectMapper.writeValueAsString(pedidoCriadoRequest)

        //when-then
        mockMvc.post("/v1/pagamentos/criar") {
            contentType = MediaType.APPLICATION_JSON
            content = json
            accept = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isBadRequest() }
            }
    }

    @Test
    fun `deve retornar 200 ok ao finalizar com sucesso`() {
        //given
        val pagamento = criarPagamento(1L)

        every { pagamentoAdapter.finalizar(any()) } returns pagamento

        val pagamentoCriadoRequest = pagamentoCriadoRequest(1L)
        val json = objectMapper.writeValueAsString(pagamentoCriadoRequest)

        //when-then
        mockMvc.post(
            String.format(
                "/v1/pagamentos/finalizar?data.id=%s&type=%s",
                pagamentoCriadoRequest.data.id,
                "payment"
            )
        ) {
            contentType = MediaType.APPLICATION_JSON
            content = json
            accept = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isOk() }
                jsonPath("$.id").value(pagamento.id)
                jsonPath("$.referenciaPedido").value(pagamento.idPedido)
                jsonPath("$.dataHora").value(pagamento.dataHora)
                jsonPath("$.status").value(pagamento.status)
                jsonPath("$.qrCode").value(pagamento.qrCode)
                jsonPath("$.valorTotal").value(pagamento.valorTotal)
            }
    }

    @Test
    fun `deve retornar 400 bad request ao finalizar quando a request tem tipo invalido`() {
        //given
        val pagamento = criarPagamento(1L)

        every { pagamentoAdapter.finalizar(any()) } returns pagamento

        val pagamentoCriadoRequest = pagamentoCriadoRequest(1L)
        val json = objectMapper.writeValueAsString(pagamentoCriadoRequest(1L))

        //when-then
        mockMvc.post(
            String.format(
                "/v1/pagamentos/finalizar?data.id=%s&type=%s",
                pagamentoCriadoRequest.data.id,
                "invalidType"
            )
        ) {
            contentType = MediaType.APPLICATION_JSON
            content = json
            accept = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isBadRequest() }
            }
    }

    @Test
    fun `deve retornar 400 bad request ao finalizar quando a request tem action invalida`() {
        //given
        val pagamento = criarPagamento(1L)

        every { pagamentoAdapter.finalizar(any()) } returns pagamento

        val pagamentoCriadoRequest = pagamentoCriadoRequest(1L)
        val json = objectMapper.writeValueAsString(pagamentoCriadoRequest.copy(action = "invalid"))

        //when-then
        mockMvc.post(
            String.format(
                "/v1/pagamentos/finalizar?data.id=%s&type=%s",
                pagamentoCriadoRequest.data.id,
                "payment"
            )
        ) {
            contentType = MediaType.APPLICATION_JSON
            content = json
            accept = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isBadRequest() }
            }
    }

    @Test
    fun `deve retornar 400 bad request ao finalizar quando a request tem type invalido`() {
        //given
        val pagamento = criarPagamento(1L)

        every { pagamentoAdapter.finalizar(any()) } returns pagamento

        val pagamentoCriadoRequest = pagamentoCriadoRequest(1L)
        val json = objectMapper.writeValueAsString(pagamentoCriadoRequest.copy(type = "invalid"))

        //when-then
        mockMvc.post(
            String.format(
                "/v1/pagamentos/finalizar?data.id=%s&type=%s",
                pagamentoCriadoRequest.data.id,
                "payment"
            )
        ) {
            contentType = MediaType.APPLICATION_JSON
            content = json
            accept = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isBadRequest() }
            }
    }

    @Test
    fun `deve retornar 400 bad request ao finalizar quando a request tem data id invalida`() {
        //given
        val pagamento = criarPagamento(1L)

        every { pagamentoAdapter.finalizar(any()) } returns pagamento

        val json = objectMapper.writeValueAsString(pagamentoCriadoRequest(1L))

        //when-then
        mockMvc.post(
            String.format(
                "/v1/pagamentos/finalizar?data.id=%s&type=%s",
                3L,
                "payment"
            )
        ) {
            contentType = MediaType.APPLICATION_JSON
            content = json
            accept = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isBadRequest() }
            }
    }

    private fun criarPedidoRequest() = PedidoCriadoMsg(
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
            dataHora = LocalDateTime.now().minusDays(1).toString(),
            status = PagamentoStatus.APROVADO,
            qrCode = Random().nextLong().toString(),
            valorTotal = BigDecimal.TEN
        )

    private fun pagamentoCriadoRequest(pagamentoId: Long): PagamentoCriadoRequest {
        val pagamentoCriadoRequest = PagamentoCriadoRequest(
            id = Random().nextLong(),
            action = "payment.created",
            apiVersion = "apiVersion",
            data = PagamentoCriadoRequest.DataRequest(id = pagamentoId.toString()),
            dateCreated = LocalDateTime.now().toString(),
            liveMode = true,
            type = "payment",
            userId = "userId"
        )
        return pagamentoCriadoRequest
    }
}