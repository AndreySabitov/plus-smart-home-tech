package ru.practicum.collector.client;

import ru.practicum.collector.events.hub.HubEvent;
import ru.practicum.collector.events.sensor.SensorEvent;

public interface CollectorClient {
    void collectSensorEvent(SensorEvent sensorEvent);

    void collectHubEvent(HubEvent hubEvent);
}
