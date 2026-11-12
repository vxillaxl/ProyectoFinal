package com.smarttraffic.dispatcher;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smarttraffic.domain.alert.ZoneStateReplica;
import com.smarttraffic.messaging.BrokerNames;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DispatcherConfig {

    @Bean
    ZoneStateReplica zoneStateReplica(LoggingAlertObserver observer) {
        return new ZoneStateReplica(List.of(observer));
    }

    @Bean
    AlertQueryService alertQueryService(ZoneStateReplica zoneStateReplica) {
        return new AlertQueryService(zoneStateReplica);
    }

    @Bean
    FanoutExchange trafficUpdates() {
        return new FanoutExchange(BrokerNames.TRAFFIC_UPDATES, true, false);
    }

    @Bean
    Queue dispatcherQueue() {
        return new Queue(BrokerNames.DISPATCHER_QUEUE, true);
    }

    @Bean
    Binding dispatcherBinding(@Qualifier("dispatcherQueue") Queue dispatcherQueue, FanoutExchange trafficUpdates) {
        return BindingBuilder.bind(dispatcherQueue).to(trafficUpdates);
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
    MessageConverter messageConverter(ObjectMapper objectMapper) {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter(objectMapper);
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTypePrecedence(Jackson2JavaTypeMapper.TypePrecedence.INFERRED);
        typeMapper.addTrustedPackages("com.smarttraffic.messaging");
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }
}
