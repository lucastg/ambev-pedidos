package com.myproject.orders.application.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String AMQ_DIRECT_EXCHANGE = "amq.direct";
    public static final String QUEUE_PEDIDOS_ENTRADA = "pedidos.entrada";
    public static final String QUEUE_PEDIDOS_SAIDA = "pedidos.saida";
    public static final String EXCHANGE_PEDIDOS = "pedidos.exchange";
    public static final String ROUTING_KEY_ENTRADA = "pedidos.entrada";
    public static final String ROUTING_KEY_SAIDA = "pedidos.saida";
    public static final String QUEUE_PEDIDOS_ENTRADA_DLQ = "pedidos.entrada.dlq";
    public static final String EXCHANGE_DLX = "dlx.exchange";

    @Bean
    public Queue pedidosEntradaQueue() {
        return QueueBuilder.durable(QUEUE_PEDIDOS_ENTRADA)
                .withArgument("x-dead-letter-exchange", EXCHANGE_DLX)
                .withArgument("x-dead-letter-routing-key", QUEUE_PEDIDOS_ENTRADA_DLQ)
                .build();
    }

    @Bean
    public Queue pedidosSaidaQueue() {
        return new Queue(QUEUE_PEDIDOS_SAIDA, true);
    }

    @Bean
    public TopicExchange pedidosExchange() {
        return new TopicExchange(EXCHANGE_PEDIDOS);
    }

    @Bean
    public Binding bindingEntrada(){
        return BindingBuilder.bind(pedidosEntradaQueue())
                .to(pedidosExchange())
                .with(ROUTING_KEY_ENTRADA);
    }

    @Bean
    public Binding bindingSaida(){
        return BindingBuilder.bind(pedidosSaidaQueue())
                .to(pedidosExchange())
                .with(ROUTING_KEY_SAIDA);
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }


    @Bean
    public Queue pedidosEntradaDlqQueue() {
        return new Queue(QUEUE_PEDIDOS_ENTRADA_DLQ, true);
    }

    @Bean
    public DirectExchange dlxExchange() {
        return new DirectExchange(EXCHANGE_DLX);
    }

    @Bean
    public Binding dlqBinding(Queue pedidosEntradaDlqQueue, DirectExchange dlxExchange) {
        return BindingBuilder.bind(pedidosEntradaDlqQueue).to(dlxExchange).with(QUEUE_PEDIDOS_ENTRADA_DLQ);
    }

    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory(ConnectionFactory connectionFactory) {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory);
        factory.setMessageConverter(jsonMessageConverter());
        factory.setDefaultRequeueRejected(false);
        return factory;
    }

    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }
}
