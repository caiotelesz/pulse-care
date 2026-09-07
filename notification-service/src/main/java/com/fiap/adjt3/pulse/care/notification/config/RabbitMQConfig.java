package com.fiap.adjt3.pulse.care.notification.config;

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
    JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter();
    // O scheduling-service publica com o nome da classe dele (pacote diferente) no
    // header __TypeId__. Como o notification-service não tem essa classe no
    // classpath, ignoramos o header e inferimos o tipo pelo parâmetro do @RabbitListener.
    converter.setTypePrecedence(TypePrecedence.INFERRED);
    return converter;
  }
}
