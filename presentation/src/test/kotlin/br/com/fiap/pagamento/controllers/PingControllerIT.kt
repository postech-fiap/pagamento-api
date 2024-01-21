package br.com.fiap.pagamento.controllers

import br.com.fiap.pagamento.config.IntegrationTest
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.get

class PingControllerIT : IntegrationTest() {

    @Test
    fun `deve retornar 200 ok ao buscar o ping com sucesso`() {
        //when-then
        mockMvc.get("/ping") {
            contentType = MediaType.APPLICATION_JSON
        }
            .andExpect {
                status { isOk() }
            }
    }
}