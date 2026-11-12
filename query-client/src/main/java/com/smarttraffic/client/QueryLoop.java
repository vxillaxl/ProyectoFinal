package com.smarttraffic.client;

import com.smarttraffic.domain.model.ClientId;
import com.smarttraffic.messaging.BrokerNames;
import com.smarttraffic.messaging.QueryAnswerRecord;
import com.smarttraffic.messaging.QueryRequestRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QueryLoop {

    private static final Logger log = LoggerFactory.getLogger(QueryLoop.class);

    private final ClientId clientId;
    private final RoutePlan routes;
    private final RabbitTemplate rabbit;

    public QueryLoop(ClientId clientId, RoutePlan routes, RabbitTemplate rabbit) {
        this.clientId = clientId;
        this.routes = routes;
        this.rabbit = rabbit;
    }

    @Scheduled(
            initialDelayString = "${traffic.query-initial-delay-ms:12000}",
            fixedRateString = "${traffic.query-interval-ms:15000}"
    )
    public void ask() {
        List<String> trayecto = routes.next();
        QueryRequestRecord request = new QueryRequestRecord(clientId.value(), trayecto);
        rabbit.convertAndSend("", BrokerNames.QUERY_QUEUE, request);
        log.info("Pregunta de {} por {}", clientId.value(), trayecto);
    }

    @RabbitListener(queues = "#{answerQueue.name}")
    public void onAnswer(QueryAnswerRecord answer) {
        if (answer.error() != null) {
            log.warn("Respuesta para {}: {}", clientId.value(), answer.error());
            return;
        }
        log.info("{}", answer.summary());
    }
}
