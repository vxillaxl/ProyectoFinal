package com.smarttraffic.processor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smarttraffic.domain.density.DensityStrategies;
import com.smarttraffic.domain.zone.CityTrafficBoard;
import com.smarttraffic.messaging.BrokerNames;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import java.time.Clock;

@Configuration
public class ProcessorConfig {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    CityTrafficBoard cityTrafficBoard(
            @Value("${traffic.density.strategy}") String strategy,
            @Value("${traffic.density.window-size}") int windowSize
    ) {
        return new CityTrafficBoard(DensityStrategies.named(strategy), windowSize);
    }

    @Bean
    FanoutExchange trafficUpdates() {
        return new FanoutExchange(BrokerNames.TRAFFIC_UPDATES, true, false);
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

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
            ConsumerFactory<String, String> consumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(new DefaultErrorHandler(new FixedBackOff(0L, 0L)));
        return factory;
    }
}
