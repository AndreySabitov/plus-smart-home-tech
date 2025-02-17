package ru.practicum.collector.handlers.hub.proto;

import org.springframework.stereotype.Component;
import ru.practicum.collector.producer.KafkaEventProtoProducer;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

@Component
public class ScenarioRemovedHandlerProto extends BaseHubEventHandlerProto {
    public ScenarioRemovedHandlerProto(KafkaEventProtoProducer producer) {
        super(producer);
    }

    @Override
    public HubEventProto.PayloadCase getMessageType() {
        return HubEventProto.PayloadCase.SCENARIO_REMOVED;
    }
}
