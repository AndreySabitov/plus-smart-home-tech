package ru.practicum.collector.handlers.hub;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Value;
import ru.practicum.collector.events.hub.HubEvent;
import ru.practicum.collector.producer.KafkaEventProducer;

@RequiredArgsConstructor
public abstract class BaseHubHandler implements HubEventHandler {
    private final KafkaEventProducer producer;
    @Value("${topic.telemetry-hubs}")
    private String topic;

    @Override
    public void handle(HubEvent event) {
        producer.send(toAvro(event), event.getHubId(), event.getTimestamp(), topic);
    }

    public abstract SpecificRecordBase toAvro(HubEvent hubEvent);
}
