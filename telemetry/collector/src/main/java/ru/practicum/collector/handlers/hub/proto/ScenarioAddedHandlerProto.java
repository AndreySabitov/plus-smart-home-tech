package ru.practicum.collector.handlers.hub.proto;

import org.springframework.stereotype.Component;
import ru.practicum.collector.producer.KafkaEventProtoProducer;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

@Component
public class ScenarioAddedHandlerProto extends BaseHubEventHandlerProto {
    public ScenarioAddedHandlerProto(KafkaEventProtoProducer producer) {
        super(producer);
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_ADDED;
    }
}
