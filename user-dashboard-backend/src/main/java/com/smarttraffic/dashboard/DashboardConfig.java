package com.smarttraffic.dashboard;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.smarttraffic.messaging.BrokerNames;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class DashboardConfig implements WebSocketConfigurer {

    private final DashboardBroadcaster broadcaster;

    public DashboardConfig(DashboardBroadcaster broadcaster) {
        this.broadcaster = broadcaster;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(broadcaster, "/ws/traffic").setAllowedOrigins("*");
    }

    @Bean
    FanoutExchange trafficUpdates() {
        return new FanoutExchange(BrokerNames.TRAFFIC_UPDATES, true, false);
    }

    @Bean
    Queue dashboardQueue() {
        return new Queue(BrokerNames.DASHBOARD_QUEUE, true);
    }

    @Bean
    Binding dashboardBinding(Queue dashboardQueue, FanoutExchange trafficUpdates) {
        return BindingBuilder.bind(dashboardQueue).to(trafficUpdates);
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
