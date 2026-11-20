package com.smarttraffic.config;


import org.springframework.context.annotation.Profile;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.smarttraffic.service.DashboardBroadcaster;
import com.smarttraffic.config.BrokerNames;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

import java.util.Arrays;

@Configuration
@EnableWebSocket
@Profile("dashboard")
public class DashboardConfig implements WebSocketConfigurer {

    private final DashboardBroadcaster broadcaster;
    private final String[] allowedOrigins;

    public DashboardConfig(
            DashboardBroadcaster broadcaster,
            @Value("${traffic.ws-allowed-origins:http://localhost,http://127.0.0.1}") String allowedOrigins
    ) {
        this.broadcaster = broadcaster;
        this.allowedOrigins = Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isEmpty())
                .toArray(String[]::new);
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(broadcaster, "/ws/traffic").setAllowedOrigins(allowedOrigins);
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
        typeMapper.addTrustedPackages("com.smarttraffic.dto");
        converter.setJavaTypeMapper(typeMapper);
        return converter;
    }
}
