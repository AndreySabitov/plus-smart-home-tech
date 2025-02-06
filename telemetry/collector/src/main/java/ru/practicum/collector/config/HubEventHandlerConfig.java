package ru.practicum.collector.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.collector.enums.hub.HubEventType;
import ru.practicum.collector.handlers.hub.*;

import java.util.HashMap;
import java.util.Map;

@Configuration
@RequiredArgsConstructor
public class HubEventHandlerConfig {
    private final DeviceAddedHandler deviceAddedHandler;
    private final DeviceRemovedHandler deviceRemovedHandler;
    private final ScenarioAddedHandler scenarioAddedHandler;
    private final ScenarioRemovedHandler scenarioRemovedEvent;

    @Bean
    public Map<HubEventType, HubEventHandler> getHubEventHandlers() {
        Map<HubEventType, HubEventHandler> hubEventHandlers = new HashMap<>();

        hubEventHandlers.put(HubEventType.DEVICE_ADDED, deviceAddedHandler);
        hubEventHandlers.put(HubEventType.DEVICE_REMOVED, deviceRemovedHandler);
        hubEventHandlers.put(HubEventType.SCENARIO_ADDED, scenarioAddedHandler);
        hubEventHandlers.put(HubEventType.SCENARIO_REMOVED, scenarioRemovedEvent);

        return hubEventHandlers;
    }
}
