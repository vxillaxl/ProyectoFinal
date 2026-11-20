package com.smarttraffic.listener;


import org.springframework.context.annotation.Profile;
import com.smarttraffic.service.AlertQueryService;
import com.smarttraffic.exception.ZoneNotFoundException;
import com.smarttraffic.domain.query.RouteAnswer;
import com.smarttraffic.config.BrokerNames;
import com.smarttraffic.dto.CityStateRecord;
import com.smarttraffic.dto.QueryAnswerRecord;
import com.smarttraffic.dto.QueryRequestRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@Profile("dispatcher")
public class DispatchListeners {

    private static final Logger log = LoggerFactory.getLogger(DispatchListeners.class);

    private final AlertQueryService service;
    private final RabbitTemplate rabbit;

    public DispatchListeners(AlertQueryService service, RabbitTemplate rabbit) {
        this.service = service;
        this.rabbit = rabbit;
    }

    @RabbitListener(queues = BrokerNames.DISPATCHER_QUEUE)
    public void onState(CityStateRecord state) {
        service.absorb(state.toDomain());
    }

    @RabbitListener(queues = BrokerNames.QUERY_QUEUE)
    public void onQuery(QueryRequestRecord request) {
        try {
            RouteAnswer answer = service.answer(request.toDomain());
            publish(answer.clientId().answerRoutingKey(), QueryAnswerRecord.from(answer));
            log.info("{}", answer.summary());
        } catch (ZoneNotFoundException exception) {
            log.warn("{}", exception.getMessage());
            replyFailure(request, exception.getMessage());
        } catch (RuntimeException exception) {
            log.warn("Consulta rechazada: {}", exception.getMessage());
            replyFailure(request, exception.getMessage());
        }
    }

    private void replyFailure(QueryRequestRecord request, String message) {
        if (request == null || request.userId() == null || request.userId().isBlank()) {
            return;
        }
        publish("answer." + request.userId().trim(), QueryAnswerRecord.failure(request.userId(), message));
    }

    private void publish(String routingKey, QueryAnswerRecord answer) {
        rabbit.convertAndSend(BrokerNames.QUERY_ANSWERS, routingKey, answer);
    }
}
