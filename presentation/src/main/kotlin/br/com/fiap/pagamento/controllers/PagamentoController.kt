package br.com.fiap.pagamento.controllers

import br.com.fiap.pagamento.adapters.interfaces.PagamentoAdapter
import br.com.fiap.pagamento.models.Pagamento
import br.com.fiap.pagamento.requests.PagamentoCriadoRequest
import br.com.fiap.pagamento.requests.PedidoCriadoMsg
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

private const val PAYMENT = "payment"

@RestController
@RequestMapping("v1/pagamentos")
class PagamentoController(
    private val pagamentoAdapter: PagamentoAdapter
) {

    @PostMapping("/finalizar")
    fun finalizar(
        @RequestParam("data.id") dataId: String,
        @RequestParam("type") type: String,
        @RequestBody request: PagamentoCriadoRequest
    ): ResponseEntity<Pagamento> {

        require(PAYMENT.equals(type, true)) { "O type deve ser payment " }
        require(dataId.equals(request.data.id, true)) { "O data.id param deve ser o mesmo da request" }

        return ResponseEntity.ok().body(pagamentoAdapter.finalizar(request.valid()))
    }

}
