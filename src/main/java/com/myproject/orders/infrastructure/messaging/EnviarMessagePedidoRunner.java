package com.myproject.orders.infrastructure.messaging;


import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Component
@Profile("sender")
@AllArgsConstructor
@NoArgsConstructor(force = true)
public class EnviarMessagePedidoRunner implements CommandLineRunner {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private static final String QUEUE_NAME = "pedidos.entrada";

    private static final String FILE_PATH = "pedidos.jsonl";

    private static final Logger logger = LoggerFactory.getLogger(EnviarMessagePedidoRunner.class);

    @Override
    public void run(String... args) throws Exception {
        logger.info("-----------------------------------------------------");
        logger.info("Iniciando PedidoMessageSenderRunner (profile 'sender' ativo)");
        logger.info("Lendo pedidos do arquivo: {}" , FILE_PATH);
        logger.info("Enviando para a fila: {}" , QUEUE_NAME);
        logger.info("-----------------------------------------------------");

        int messagesSent = 0;

        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String trimmedLine = line.trim();
                if (!trimmedLine.isEmpty()) {
                    MessageProperties properties = new MessageProperties();
                    properties.setContentType(MessageProperties.CONTENT_TYPE_JSON);
                    properties.setContentEncoding("UTF-8");
                    byte[] jsonBytes = trimmedLine.getBytes(StandardCharsets.UTF_8);
                    Message message = MessageBuilder.withBody(jsonBytes)
                            .andProperties(properties)
                            .build();
                    rabbitTemplate.send(QUEUE_NAME, message);

                    messagesSent++;
                    logger.info(" [x] Enviado pedido #{}: '{}...' ", messagesSent, trimmedLine.substring(0, Math.min(trimmedLine.length(), 80)));
                }
            }
            logger.info("\n--- Concluído o envio de {} pedidos. ---", messagesSent);

        } catch (IOException e) {
            logger.error("Erro ao ler o arquivo de pedidos: {} - {}", FILE_PATH, e.getMessage(), e);
            logger.error("Certifique-se de que o arquivo existe e o caminho está correto.");
        } catch (Exception e) {
            logger.error("Ocorreu um erro inesperado durante o envio: {}", e.getMessage(), e);
        } finally {
            logger.info("-----------------------------------------------------");
        }
    }
}
