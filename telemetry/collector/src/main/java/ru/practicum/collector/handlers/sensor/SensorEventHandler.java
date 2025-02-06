package ru.practicum.collector.handlers.sensor;

import ru.practicum.collector.events.sensor.SensorEvent;

public interface SensorEventHandler {
    void handle(SensorEvent event);
}
