package br.com.fiap.pagamento.config

import br.com.fiap.pagamento.interfaces.PagamentoRepository
import br.com.fiap.pagamento.interfaces.gateways.BuscarPagamentoPorIdGateway
import br.com.fiap.pagamento.interfaces.gateways.GerarQrCodePagamentoGateway
import br.com.fiap.pagamento.interfaces.usecases.FinalizarPagamentoUseCase
import br.com.fiap.pagamento.usecases.FinalizarPagamentoUseCaseImpl
import br.com.fiap.pagamento.usecases.GerarQrCodePagamentoUseCaseImpl
import br.com.fiap.pagamento.gateways.BuscarPagamentoPorIdHttpGatewayImpl
import br.com.fiap.pagamento.gateways.GerarQrCodePagamentoHttpGatewayImpl
import br.com.fiap.pagamento.repositories.PagamentoRepositoryImpl
import br.com.fiap.pagamento.repositories.mongo.PagamentoMongoRepository
import br.com.fiap.pagamento.adapters.PagamentoAdapterImpl
import br.com.fiap.pagamento.gateways.AtualizarPedidoHttpGatewayImpl
import br.com.fiap.pagamento.interfaces.gateways.AtualizarPedidoGateway
import br.com.fiap.pagamento.interfaces.usecases.AlterarStatusPagamentoUseCase
import br.com.fiap.pagamento.interfaces.usecases.ConsultarPagamentoUseCase
import br.com.fiap.pagamento.interfaces.usecases.GerarQrCodePagamentoUseCase
import br.com.fiap.pagamento.usecases.AlterarStatusPagamentoUseCaseImpl
import br.com.fiap.pagamento.usecases.ConsultarPagamentoUseCaseImpl
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.client.RestTemplate

@Configuration
@EntityScan(basePackages = ["br.com.fiap.pagamento.*"])
class AppBeansConfig(
    val endpointConfig: EndpointConfig,
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
        endpointConfig.pedidosApiAtualizarEndpoint
    )

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
    fun finalizarPagamentoUseCase(
        buscarPagamentoPorIdGateway: BuscarPagamentoPorIdGateway,
        alterarStatusPagamentoUseCase: AlterarStatusPagamentoUseCase,
        atualizarPedidoGateway: AtualizarPedidoGateway,
    ) = FinalizarPagamentoUseCaseImpl(buscarPagamentoPorIdGateway, alterarStatusPagamentoUseCase, atualizarPedidoGateway)

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
