package ru.practicum.collector.handlers.sensor.proto;

import org.springframework.stereotype.Component;
import ru.practicum.collector.producer.KafkaEventProtoProducer;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

@Component
public class SwitchSensorHandlerProto extends BaseSensorHandlerProto {
    public SwitchSensorHandlerProto(KafkaEventProtoProducer producer) {
        super(producer);
    }

    @Override
    public SensorEventProto.PayloadCase getMessageType() {
        return SensorEventProto.PayloadCase.SWITCH_SENSOR_EVENT;
    }
}
