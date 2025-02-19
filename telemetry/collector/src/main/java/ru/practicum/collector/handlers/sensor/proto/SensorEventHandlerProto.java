package ru.practicum.collector.handlers.sensor.proto;

import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

public interface SensorEventHandlerProto {
    SensorEventProto.PayloadCase getMessageType();

    void handle(SensorEventProto event);
}
