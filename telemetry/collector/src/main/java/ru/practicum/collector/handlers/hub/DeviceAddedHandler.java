package ru.practicum.collector.handlers.hub;

import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.practicum.collector.enums.hub.DeviceType;
import ru.practicum.collector.events.hub.HubEvent;
import ru.practicum.collector.events.hub.device.DeviceAddedEvent;
import ru.practicum.collector.producer.KafkaEventProducer;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;

@Component
public class DeviceAddedHandler extends BaseHubHandler {
    public DeviceAddedHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public SpecificRecordBase toAvro(HubEvent hubEvent) {
        DeviceAddedEvent event = (DeviceAddedEvent) hubEvent;

        return HubEventAvro.newBuilder()
                .setHubId(hubEvent.getHubId())
                .setTimestamp(hubEvent.getTimestamp())
                .setPayload(new DeviceAddedEventAvro(event.getId(), mapToDeviceTypeAvro(event.getDeviceType())))
                .build();
    }

    private DeviceTypeAvro mapToDeviceTypeAvro(DeviceType deviceType) {
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
}
