package ru.practicum.collector.handlers.sensor;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Value;
import ru.practicum.collector.producer.KafkaEventProducer;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;

import java.time.Instant;

@RequiredArgsConstructor
public abstract class BaseSensorHandlerProto implements SensorEventHandlerProto {
    private final KafkaEventProducer producer;
    @Value("${topic.telemetry-sensors}")
    private String topic;

    @Override
    public void handle(SensorEventProto event) {
        producer.send(toAvro(event), event.getHubId(), mapTimestampToInstant(event), topic);
    }

    public abstract SensorEventAvro toAvro(SensorEventProto sensorEvent);

    public Instant mapTimestampToInstant(SensorEventProto event) {
        return Instant.ofEpochSecond(event.getTimestamp().getSeconds(), event.getTimestamp().getNanos());
    }
}
