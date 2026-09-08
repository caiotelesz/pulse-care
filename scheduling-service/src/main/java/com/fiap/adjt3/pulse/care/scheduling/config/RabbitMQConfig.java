package com.fiap.adjt3.pulse.care.scheduling.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

  @Value("${rabbitmq.queue.appointment}")
  private String appointmentQueue;

  @Value("${rabbitmq.exchange.appointment}")
  private String appointmentExchange;

  @Value("${rabbitmq.routing.key.appointment}")
  private String appointmentRoutingKey;

  @Bean
  public Queue appointmentQueue() {
    return new Queue(appointmentQueue, true);
  }

  @Bean
  public DirectExchange appointmentExchange() {
    return new DirectExchange(appointmentExchange);
  }

  @Bean
  public Binding appointmentBinding(Queue appointmentQueue, DirectExchange appointmentExchange) {
    return BindingBuilder.bind(appointmentQueue).to(appointmentExchange).with(appointmentRoutingKey);
  }

  @Bean
  public JacksonJsonMessageConverter messageConverter() {
    return new JacksonJsonMessageConverter();
  }

  @Bean
  public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory,
      JacksonJsonMessageConverter messageConverter) {
    RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
    rabbitTemplate.setMessageConverter(messageConverter);
    return rabbitTemplate;
  }
}
