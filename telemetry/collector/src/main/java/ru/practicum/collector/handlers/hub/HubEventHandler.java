package ru.practicum.collector.handlers.hub;

import ru.practicum.collector.events.hub.HubEvent;

public interface HubEventHandler {
    void handle(HubEvent hubEvent);
}
