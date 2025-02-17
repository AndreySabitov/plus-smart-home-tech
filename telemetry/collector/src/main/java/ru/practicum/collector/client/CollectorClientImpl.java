package ru.practicum.collector.client;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.collector.enums.hub.HubEventType;
import ru.practicum.collector.enums.sensor.SensorEventType;
import ru.practicum.collector.events.hub.HubEvent;
import ru.practicum.collector.events.sensor.SensorEvent;
import ru.practicum.collector.handlers.hub.avro.HubEventHandler;
import ru.practicum.collector.handlers.sensor.avro.SensorEventHandler;

import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class CollectorClientImpl implements CollectorClient {
    private final Map<SensorEventType, SensorEventHandler> sensorEventHandlers;
    private final Map<HubEventType, HubEventHandler> hubEventHandlers;

    @Override
    public void collectSensorEvent(SensorEvent sensorEvent) {
        sensorEventHandlers.get(sensorEvent.getType()).handle(sensorEvent);
    }

    @Override
    public void collectHubEvent(HubEvent hubEvent) {
        hubEventHandlers.get(hubEvent.getType()).handle(hubEvent);
    }
}
