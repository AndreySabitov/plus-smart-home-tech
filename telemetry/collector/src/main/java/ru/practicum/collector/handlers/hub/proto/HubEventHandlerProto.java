package ru.practicum.collector.handlers.hub.proto;

import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;

public interface HubEventHandlerProto {
    HubEventProto.PayloadCase getMessageType();

    void handle(HubEventProto event);
}
