package ru.practicum.collector.events.hub.scenario;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import ru.practicum.collector.enums.hub.DeviceActionType;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
public class DeviceAction {
    String sensorId;
    DeviceActionType type;
    Integer value;
}
