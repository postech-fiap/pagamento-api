# language: pt
Funcionalidade: Pagamento

  Cenario: Criar pagamento
    Quando eu solicitar a geracao do pagamento de um pedido criado
    Entao deve retornar o qr code para pagamento com status PENDENTE

  Cenario: Finalizar pagamento
    Quando a api receber a notificacao de conclusao do pagamento
    Entao deve atualizar o status do pagamento com sucesso
