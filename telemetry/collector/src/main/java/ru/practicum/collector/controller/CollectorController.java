package ru.practicum.collector.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.collector.events.hub.HubEvent;
import ru.practicum.collector.events.sensor.SensorEvent;

@RestController
@RequestMapping("/events")
@Slf4j
public class CollectorController {

    @PostMapping("/sensors")
    public void collectSensorEvent(@Valid @RequestBody SensorEvent sensorEvent) {
        log.info("Получили событие из датчика {}", sensorEvent.getType());
    }

    @PostMapping("/hubs")
    public void collectHubEvent(@Valid @RequestBody HubEvent hubEvent) {
        log.info("Получили событие хаба: {}", hubEvent.getType());
    }
}
