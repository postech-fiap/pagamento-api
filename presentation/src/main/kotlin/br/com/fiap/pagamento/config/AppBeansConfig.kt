package br.com.fiap.pagamento.config

import br.com.fiap.pagamento.adapters.PagamentoAdapterImpl
import br.com.fiap.pagamento.gateways.http.AtualizarPedidoHttpGatewayImpl
import br.com.fiap.pagamento.gateways.http.BuscarPagamentoPorIdHttpGatewayImpl
import br.com.fiap.pagamento.gateways.http.GerarQrCodePagamentoHttpGatewayImpl
import br.com.fiap.pagamento.gateways.rabbitmq.EnviarMensagemPagamentoRabbitMqGatewayImpl
import br.com.fiap.pagamento.interfaces.PagamentoRepository
import br.com.fiap.pagamento.interfaces.gateways.BuscarPagamentoPorIdGateway
import br.com.fiap.pagamento.interfaces.gateways.EnviarMensagemPagamentoGateway
import br.com.fiap.pagamento.interfaces.gateways.GerarQrCodePagamentoGateway
import br.com.fiap.pagamento.interfaces.gateways.PagamentoFinalizadoServiceI
import br.com.fiap.pagamento.interfaces.usecases.AlterarStatusPagamentoUseCase
import br.com.fiap.pagamento.interfaces.usecases.ConsultarPagamentoUseCase
import br.com.fiap.pagamento.interfaces.usecases.FinalizarPagamentoUseCase
import br.com.fiap.pagamento.interfaces.usecases.GerarQrCodePagamentoUseCase
import br.com.fiap.pagamento.repositories.mongo.PagamentoMongoRepository
import br.com.fiap.pagamento.repositories.mongo.PagamentoRepositoryImpl
import br.com.fiap.pagamento.services.PagamentoFinalizadoService
import br.com.fiap.pagamento.usecases.AlterarStatusPagamentoUseCaseImpl
import br.com.fiap.pagamento.usecases.ConsultarPagamentoUseCaseImpl
import br.com.fiap.pagamento.usecases.FinalizarPagamentoUseCaseImpl
import br.com.fiap.pagamento.usecases.GerarQrCodePagamentoUseCaseImpl
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
@EntityScan(basePackages = ["br.com.fiap.pagamento.*"])
class AppBeansConfig(
    val endpointConfig: EndpointConfig,
    val rabbitMqConfig: RabbitMqConfig,
    val restTemplate: RestTemplate
) {

    @Bean
    fun pagamentoRepository(pagamentoMongoRepository: PagamentoMongoRepository) =
        PagamentoRepositoryImpl(pagamentoMongoRepository)

    @Bean
    fun gerarQrCodePagamentoGateway() = GerarQrCodePagamentoHttpGatewayImpl(
        restTemplate,
        endpointConfig.mercadoPagoQrcodeEndpoint,
        endpointConfig.mercadoPagotoken,
        endpointConfig.mercadoPagowebhookUrl
    )

    @Bean
    fun buscarPagamentoPorIdGateway() = BuscarPagamentoPorIdHttpGatewayImpl(
        restTemplate,
        endpointConfig.mercadoPagoOrdersPagamentoEndpoint,
        endpointConfig.mercadoPagotoken
    )

    @Bean
    fun atualizarPedidoGateway() = AtualizarPedidoHttpGatewayImpl(
        restTemplate,
        "pedidosApiAtualizarEndpoint"
    )

    @Bean
    fun enviarMensagemPagamentoGateway(
        objectMapper: ObjectMapper
    ) = EnviarMensagemPagamentoRabbitMqGatewayImpl(
        rabbitMqConfig.rabbitTemplate,
        rabbitMqConfig.pagamentoFinalizadoQueue(),
        objectMapper)

    @Bean
    fun gerarQrCodePagamentoUseCase(gerarQrCodePagamentoGateway: GerarQrCodePagamentoGateway) =
        GerarQrCodePagamentoUseCaseImpl(gerarQrCodePagamentoGateway)

    @Bean
    fun consultarPagamentoUseCase(
        pagamentoRepository: PagamentoRepository
    ) = ConsultarPagamentoUseCaseImpl(pagamentoRepository)

    @Bean
    fun alterarStatusPagamentoUseCase(
        consultarPagamentoUseCase: ConsultarPagamentoUseCase,
        pagamentoRepository: PagamentoRepository
    ) = AlterarStatusPagamentoUseCaseImpl(consultarPagamentoUseCase, pagamentoRepository)

    @Bean
    fun pagamentoFinalizadoServiceI(
        alterarStatusPagamentoUseCase: AlterarStatusPagamentoUseCase,
        enviarMensagemPagamentoGateway: EnviarMensagemPagamentoGateway
    ) = PagamentoFinalizadoService(alterarStatusPagamentoUseCase, enviarMensagemPagamentoGateway)


    @Bean
    fun finalizarPagamentoUseCase(
        buscarPagamentoPorIdGateway: BuscarPagamentoPorIdGateway,
        pagamentoFinalizadoServiceI: PagamentoFinalizadoServiceI
    ) = FinalizarPagamentoUseCaseImpl(buscarPagamentoPorIdGateway, pagamentoFinalizadoServiceI)

    @Bean
    fun pagamentoFacade(
        gerarQrCodePagamentoUseCase: GerarQrCodePagamentoUseCase,
        pagamentoRepository: PagamentoRepository,
        consultarPagamentoUseCase: ConsultarPagamentoUseCase,
        finalizarPagamentoUseCase: FinalizarPagamentoUseCase
    ) = PagamentoAdapterImpl(
        gerarQrCodePagamentoUseCase,
        pagamentoRepository,
        consultarPagamentoUseCase,
        finalizarPagamentoUseCase
    )
}
