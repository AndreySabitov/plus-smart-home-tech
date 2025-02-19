package ru.practicum.collector.handlers.hub;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Value;
import ru.practicum.collector.producer.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

import java.time.Instant;

@RequiredArgsConstructor
public abstract class BaseHubEventHandlerProto implements HubEventHandlerProto {
    private final KafkaEventProducer producer;
    @Value("${topic.telemetry-hubs}")
    private String topic;

    @Override
    public void handle(HubEventProto event) {
        producer.send(toAvro(event), event.getHubId(),
                Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()), topic);
    }

    public Instant mapTimestampToInstant(HubEventProto event) {
        return Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos());
    }

    public abstract SpecificRecordBase toAvro(HubEventProto hubEvent);
}
