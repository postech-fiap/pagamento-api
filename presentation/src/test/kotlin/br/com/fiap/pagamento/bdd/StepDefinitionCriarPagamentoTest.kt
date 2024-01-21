package br.com.fiap.pagamento.bdd

import io.cucumber.java8.Pt
import io.restassured.RestAssured.given
import io.restassured.response.Response
import org.apache.http.HttpStatus
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.CoreMatchers.notNullValue

private const val ENDPOINT = "http://localhost:8080/v1/pagamentos/criar"

class StepDefinitionCriarPagamentoTest : Pt {

    lateinit var response: Response

    init {
        Quando("eu solicitar a geracao do pagamento de um pedido criado") {
            response = given()
                .contentType("application/json")
                .body(
                    """
                    {
                        "id": 10,
                        "numero": "11",
                        "data_hora": "2024-02-06T15:30:30Z",
                        "items": [{
                            "quantidade": 1,
                            "produto": {
                                "id": 123,
                                "nome": "produto",
                                "descricao": "descricao",
                                "categoria": "LANCHE",
                                "valor": 10
                            },
                            "valor_pago": 10
                        }],
                         "valor_total": 10
                    }
                """.trimIndent()
                )
                .`when`()
                .post(ENDPOINT)
        }
        Entao("deve retornar o qr code para pagamento com status PENDENTE") {
            response.then()
                .statusCode(HttpStatus.SC_CREATED)
                .body("status", equalTo("PENDENTE"))
                .body("qr_code", notNullValue())
        }
    }

}