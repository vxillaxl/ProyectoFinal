package com.smarttraffic.archiver;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import java.time.Clock;

@Configuration
public class ArchiverConfig {

    @Bean
    Clock clock() {
        return Clock.systemUTC();
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory(
            ConsumerFactory<String, String> consumerFactory
    ) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        // Kafka es el registro; si PostgreSQL se cae un momento se reintenta en vez de perder lecturas,
        // y un mensaje malformado se descarta sin reintentos.
        DefaultErrorHandler handler = new DefaultErrorHandler(new FixedBackOff(2000L, 30L));
        handler.addNotRetryableExceptions(JsonProcessingException.class, IllegalArgumentException.class);
        factory.setCommonErrorHandler(handler);
        return factory;
    }
}
