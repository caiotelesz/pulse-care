package com.fiap.adjt3.pulse.care.history.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.JacksonJavaTypeMapper.TypePrecedence;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

  @Value("${rabbitmq.queue.appointment-history}")
  private String appointmentHistoryQueue;

  @Value("${rabbitmq.exchange.appointment}")
  private String appointmentExchange;

  @Value("${rabbitmq.routing.key.appointment}")
  private String appointmentRoutingKey;

  @Bean
  public Queue appointmentHistoryQueue() {
    return new Queue(appointmentHistoryQueue, true);
  }

  @Bean
  public DirectExchange appointmentExchange() {
    return new DirectExchange(appointmentExchange);
  }

  @Bean
  public Binding appointmentHistoryBinding(Queue appointmentHistoryQueue, DirectExchange appointmentExchange) {
    return BindingBuilder.bind(appointmentHistoryQueue).to(appointmentExchange).with(appointmentRoutingKey);
  }

  @Bean
  public JacksonJsonMessageConverter messageConverter() {
    JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter();
    converter.setTypePrecedence(TypePrecedence.INFERRED);
    return converter;
  }
}
