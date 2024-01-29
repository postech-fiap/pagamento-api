package br.com.fiap.pagamento.bdd

import io.cucumber.java8.Pt
import io.restassured.RestAssured
import io.restassured.response.Response
import org.apache.http.HttpStatus
import org.hamcrest.CoreMatchers

private const val ENDPOINT = "http://localhost:8080/v1/pagamentos/finalizar"

class StepDefinitionFinalizarPagamentoTest : Pt {

    lateinit var response: Response

    init {
        Quando("a api receber a notificacao de conclusao do pagamento") {
            response = RestAssured.given()
                .contentType("application/json")
                .queryParam("data.id", "123")
                .queryParam("type", "payment")
                .body(
                    """
                    {
                        "action": "payment.created",
                        "api_version": "v1",
                        "data": { "id":"123" },
                        "date_created": "2023-08-28T18:11:04Z",
                        "id": 1234,
                        "live_mode": true,
                        "type": "payment",
                        "user_id": "12345"
                    }
                """.trimIndent()
                )
                .`when`()
                .post(ENDPOINT)
        }
        Entao("deve atualizar o status do pagamento com sucesso") {
            response.then()
                .statusCode(HttpStatus.SC_OK)
                .body("status", CoreMatchers.equalTo("APROVADO"))
                .body("qr_code", CoreMatchers.notNullValue())
        }
    }

}