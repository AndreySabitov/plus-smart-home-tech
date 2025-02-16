package ru.practicum.collector.handlers.sensor.proto;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import ru.practicum.collector.producer.KafkaEventProtoProducer;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.time.Instant;

@RequiredArgsConstructor
public abstract class BaseSensorHandlerProto implements SensorEventHandlerProto {
    private final KafkaEventProtoProducer producer;
    @Value("${topic.telemetry-sensors}")
    private String topic;

    @Override
    public void handle(SensorEventProto event) {
        producer.send(event, event.getHubId(),
                Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos()), topic);
    }
}
