package ru.practicum.collector.handlers.hub.proto;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import ru.practicum.collector.producer.KafkaEventProtoProducer;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

import java.time.Instant;

@RequiredArgsConstructor
public abstract class BaseHubEventHandlerProto implements HubEventHandlerProto {
    private final KafkaEventProtoProducer producer;
    @Value("${topic.telemetry-hubs}")
    private String topic;

    @Override
    public void handle(HubEventProto event) {
        producer.send(event, event.getHubId(),
                Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()), topic);
    }
}
