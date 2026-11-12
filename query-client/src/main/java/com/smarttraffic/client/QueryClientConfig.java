package com.smarttraffic.client;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smarttraffic.domain.model.ClientId;
import com.smarttraffic.messaging.BrokerNames;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueryClientConfig {

    @Bean
    ClientId clientId(@Value("${traffic.client-id}") String clientId) {
        return new ClientId(clientId);
    }

    @Bean
    RoutePlan routePlan(@Value("${traffic.routes}") String routes) {
        return RoutePlan.parse(routes);
    }

    @Bean
    Queue queryTrafficQueue() {
        return QueueBuilder.durable(BrokerNames.QUERY_QUEUE).build();
    }

    @Bean
    TopicExchange queryAnswers() {
        return new TopicExchange(BrokerNames.QUERY_ANSWERS, true, false);
    }

    @Bean
    Queue answerQueue(ClientId clientId) {
        return new Queue(clientId.answerRoutingKey(), false, true, true);
    }

    @Bean
    Binding answerBinding(@Qualifier("answerQueue") Queue answerQueue, TopicExchange queryAnswers, ClientId clientId) {
        return BindingBuilder.bind(answerQueue).to(queryAnswers).with(clientId.answerRoutingKey());
    }

    @Bean
    MessageConverter messageConverter(ObjectMapper objectMapper) {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.INFERRED);
        typeMapper.addTrustedPackages("com.smarttraffic.messaging");
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }
}
