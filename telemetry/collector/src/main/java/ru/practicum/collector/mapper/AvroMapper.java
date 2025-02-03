package ru.practicum.collector.mapper;

import lombok.experimental.UtilityClass;
import ru.practicum.collector.enums.hub.DeviceType;
import ru.practicum.collector.events.hub.scenario.DeviceAction;
import ru.practicum.collector.events.hub.scenario.ScenarioCondition;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.List;

@UtilityClass
public class AvroMapper {
    public DeviceTypeAvro mapToDeviceTypeAvro(DeviceType deviceType) {
        DeviceTypeAvro type = null;

        switch (deviceType) {
            case LIGHT_SENSOR -> type = DeviceTypeAvro.LIGHT_SENSOR;
            case MOTION_SENSOR -> type = DeviceTypeAvro.MOTION_SENSOR;
            case SWITCH_SENSOR -> type = DeviceTypeAvro.SWITCH_SENSOR;
            case CLIMATE_SENSOR -> type = DeviceTypeAvro.CLIMATE_SENSOR;
            case TEMPERATURE_SENSOR -> type = DeviceTypeAvro.TEMPERATURE_SENSOR;
        }
        return type;
    }

    public List<ScenarioConditionAvro> mapToConditionTypeAvro(List<ScenarioCondition> conditions) {
        return conditions.stream()
                .map(c -> ScenarioConditionAvro.newBuilder()
                        .setSensorId(c.getSensorId())
                        .setType(
                                switch (c.getType()) {
                                    case MOTION -> ConditionTypeAvro.MOTION;
                                    case LUMINOSITY -> ConditionTypeAvro.LUMINOSITY;
                                    case SWITCH -> ConditionTypeAvro.SWITCH;
                                    case TEMPERATURE -> ConditionTypeAvro.TEMPERATURE;
                                    case CO2LEVEL -> ConditionTypeAvro.CO2LEVEL;
                                    case HUMIDITY -> ConditionTypeAvro.HUMIDITY;
                                })
                        .setOperation(
                                switch (c.getOperation()) {
                                    case EQUALS -> ConditionOperationAvro.EQUALS;
                                    case GREATER_THAN -> ConditionOperationAvro.GREATER_THAN;
                                    case LOWER_THAN -> ConditionOperationAvro.LOWER_THAN;
                                }
                        )
                        .setValue(c.getValue())
                        .build())
                .toList();
    }

    public List<DeviceActionAvro> mapToDeviceActionAvro(List<DeviceAction> deviceActions) {
        return deviceActions.stream()
                .map(da -> DeviceActionAvro.newBuilder()
                        .setSensorId(da.getSensorId())
                        .setType(
                                switch (da.getType()) {
                                    case ACTIVATE -> ActionTypeAvro.ACTIVATE;
                                    case DEACTIVATE -> ActionTypeAvro.DEACTIVATE;
                                    case INVERSE -> ActionTypeAvro.INVERSE;
                                    case SET_VALUE -> ActionTypeAvro.SET_VALUE;
                                }
                        )
                        .setValue(da.getValue())
                        .build())
                .toList();
    }
}
