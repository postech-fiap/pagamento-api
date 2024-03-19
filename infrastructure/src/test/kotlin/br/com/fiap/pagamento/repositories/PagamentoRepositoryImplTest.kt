package br.com.fiap.pagamento.repositories

import br.com.fiap.pagamento.entities.PagamentoEntity
import br.com.fiap.pagamento.enums.PagamentoStatus
import br.com.fiap.pagamento.exceptions.BaseDeDadosException
import br.com.fiap.pagamento.repositories.mongo.PagamentoMongoRepository
import br.com.fiap.pagamento.repositories.mongo.PagamentoRepositoryImpl
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
class PagamentoRepositoryImplTest {

    lateinit var target: PagamentoRepositoryImpl

    @MockK
    lateinit var pagamentoMongoRepository: PagamentoMongoRepository

    @BeforeEach
    fun init() {
        target = PagamentoRepositoryImpl(
            pagamentoMongoRepository
        )
    }

    @Test
    fun `deve salvar o pagamento com sucesso`() {
        //given
        val pedidoId = 1L
        val pagamentoEntity = criarPagamentoEntity(pedidoId)
        val pagamento = pagamentoEntity.toModel()

        every { pagamentoMongoRepository.save(any()) } returns pagamentoEntity

        //when
        val result = target.salvar(pagamento)

        //then
        assertEquals(pagamentoEntity.id, result.id)
        assertEquals(pagamentoEntity.idPedido, result.idPedido)
        assertEquals(pagamentoEntity.dataHora, result.dataHora)
        assertEquals(pagamentoEntity.qrCode, result.qrCode)
        assertEquals(pagamentoEntity.status, result.status)
        assertEquals(pagamentoEntity.valorTotal, result.valorTotal)

        verify(exactly = 1) { pagamentoMongoRepository.save(pagamentoEntity) }
    }

    @Test
    fun `deve lancar uma excecao de base de dados quando a persistencia falhar`() {
        //given
        val pedidoId = 1L
        val pagamentoEntity = criarPagamentoEntity(pedidoId)
        val pagamento = pagamentoEntity.toModel()

        every { pagamentoMongoRepository.save(any()) } throws RuntimeException("Error")

        //when-then
        val exception = assertThrows(BaseDeDadosException::class.java) {
            target.salvar(pagamento)
        }

        assertEquals("Erro ao salvar o pagamento. Detalhes: Error", exception.message)

        verify(exactly = 1) { pagamentoMongoRepository.save(pagamentoEntity) }
    }

    @Test
    fun `deve retornar o pagamento de um pedido quando existir`() {
        //given
        val pedidoId = 1L
        val pagamentoEntity = criarPagamentoEntity(pedidoId)

        every { pagamentoMongoRepository.findByPedidoId(any()) } returns pagamentoEntity

        //when
        val result = target.consultarPorPedido(pedidoId)!!

        //then
        assertEquals(pagamentoEntity.id, result.id)
        assertEquals(pagamentoEntity.idPedido, result.idPedido)
        assertEquals(pagamentoEntity.dataHora, result.dataHora)
        assertEquals(pagamentoEntity.qrCode, result.qrCode)
        assertEquals(pagamentoEntity.status, result.status)
        assertEquals(pagamentoEntity.valorTotal, result.valorTotal)

        verify(exactly = 1) { pagamentoMongoRepository.findByPedidoId(pedidoId) }
    }

    @Test
    fun `deve retornar null quando o pagamento de um pedido nao existir`() {
        //given
        val pedidoId = 1L

        every { pagamentoMongoRepository.findByPedidoId(any()) } returns null

        //when
        val result = target.consultarPorPedido(pedidoId)

        //then
        assertNull(result)

        verify(exactly = 1) { pagamentoMongoRepository.findByPedidoId(pedidoId) }
    }

    @Test
    fun `deve lancar uma excecao de base de dados quando a busca falhar`() {
        //given
        val pedidoId = 1L

        every { pagamentoMongoRepository.findByPedidoId(any()) } throws RuntimeException("Error")

        //when-then
        val exception = assertThrows(BaseDeDadosException::class.java) {
            target.consultarPorPedido(pedidoId)
        }

        assertEquals("Erro ao buscar o pagamento. Detalhes: Error", exception.message)

        verify(exactly = 1) { pagamentoMongoRepository.findByPedidoId(pedidoId) }
    }

    @Test
    fun `deve atualizar o status do pagamento com sucesso`() {
        //given
        val pedidoId = 1L
        val pagamentoEntity = criarPagamentoEntity(pedidoId)
        val pagamento = pagamentoEntity.copy(status = PagamentoStatus.PENDENTE).toModel()
        val novoStatus = PagamentoStatus.APROVADO

        every { pagamentoMongoRepository.save(any()) } returns pagamentoEntity

        //when
        val result = target.alterarStatusPagamento(pagamento, novoStatus)

        //then
        assertEquals(pagamentoEntity.id, result.id)
        assertEquals(pagamentoEntity.idPedido, result.idPedido)
        assertEquals(pagamentoEntity.dataHora, result.dataHora)
        assertEquals(pagamentoEntity.qrCode, result.qrCode)
        assertEquals(novoStatus, result.status)
        assertEquals(pagamentoEntity.valorTotal, result.valorTotal)

        verify(exactly = 1) { pagamentoMongoRepository.save(pagamentoEntity) }
    }

    @Test
    fun `deve lancar uma excecao de base de dados quando a atualizacao falhar`() {
        //given
        val pedidoId = 1L
        val pagamentoEntity = criarPagamentoEntity(pedidoId)
        val pagamento = pagamentoEntity.copy(status = PagamentoStatus.PENDENTE).toModel()
        val novoStatus = PagamentoStatus.APROVADO

        every { pagamentoMongoRepository.save(any()) } throws RuntimeException("Error")

        //when-then
        val exception = assertThrows(BaseDeDadosException::class.java) {
            target.alterarStatusPagamento(pagamento, novoStatus)
        }

        assertEquals("Erro ao atualizar o status do pagamento. Detalhes: Error", exception.message)

        verify(exactly = 1) { pagamentoMongoRepository.save(pagamentoEntity) }
    }

    private fun criarPagamentoEntity(pedidoId: Long) =
            PagamentoEntity(
                    id = UUID.randomUUID().toString(),
                    idPedido = pedidoId,
                    dataHora = LocalDateTime.now().minusDays(1).toString(),
                    status = PagamentoStatus.APROVADO,
                    qrCode = Random().nextLong().toString(),
                    valorTotal = BigDecimal.TEN
            )
}