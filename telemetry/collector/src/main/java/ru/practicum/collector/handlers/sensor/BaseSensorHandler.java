package ru.practicum.collector.handlers.sensor;

import lombok.RequiredArgsConstructor;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.beans.factory.annotation.Value;
import ru.practicum.collector.events.sensor.SensorEvent;
import ru.practicum.collector.producer.KafkaEventProducer;

@RequiredArgsConstructor
public abstract class BaseSensorHandler implements SensorEventHandler {
    private final KafkaEventProducer producer;
    @Value("${topic.telemetry-sensors}")
    private String topic;

    @Override
    public void handle(SensorEvent event) {
        producer.send(toAvro(event), event.getHubId(), event.getTimestamp(), topic);
    }

    public abstract SpecificRecordBase toAvro(SensorEvent sensorEvent);
}
