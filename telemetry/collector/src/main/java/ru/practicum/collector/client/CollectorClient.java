package ru.practicum.collector.client;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.stereotype.Service;
import ru.practicum.collector.events.hub.HubEvent;
import ru.practicum.collector.events.hub.device.DeviceAddedEvent;
import ru.practicum.collector.events.hub.device.DeviceRemovedEvent;
import ru.practicum.collector.events.hub.scenario.ScenarioAddedEvent;
import ru.practicum.collector.events.hub.scenario.ScenarioRemovedEvent;
import ru.practicum.collector.events.sensor.*;
import ru.practicum.collector.mapper.AvroMapper;
import ru.practicum.collector.serializer.CollectorAvroSerializer;
import ru.practicum.collector.topics.CollectorTopics;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.util.Properties;

@Service
@Slf4j
public class CollectorClient {
    private final Producer<String, SpecificRecordBase> producer;

    public CollectorClient() {
        Properties config = new Properties();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, CollectorAvroSerializer.class);

        producer = new KafkaProducer<>(config);
    }


    public void collectSensorEvent(SensorEvent sensorEvent) {
        Object payload = null;
        switch (sensorEvent.getType()) {
            case SWITCH_SENSOR_EVENT -> {
                SwitchSensorEvent event = (SwitchSensorEvent) sensorEvent;
                payload = new SwitchSensorAvro(event.getState());
            }
            case LIGHT_SENSOR_EVENT -> {
                LightSensorEvent event = (LightSensorEvent) sensorEvent;
                payload = new LightSensorAvro(event.getLinkQuality(), event.getLuminosity());
            }
            case MOTION_SENSOR_EVENT -> {
                MotionSensorEvent event = (MotionSensorEvent) sensorEvent;
                payload = new MotionSensorAvro(event.getLinkQuality(), event.getMotion(), event.getVoltage());
            }
            case CLIMATE_SENSOR_EVENT -> {
                ClimateSensorEvent event = (ClimateSensorEvent) sensorEvent;
                payload = new ClimateSensorAvro(event.getTemperatureC(), event.getHumidity(), event.getCo2Level());
            }
            case TEMPERATURE_SENSOR_EVENT -> {
                TemperatureSensorEvent event = (TemperatureSensorEvent) sensorEvent;
                payload = new TemperatureSensorAvro(event.getTemperatureC(), event.getTemperatureF());
            }
        }

        SensorEventAvro message = SensorEventAvro.newBuilder()
                .setId(sensorEvent.getId())
                .setHubId(sensorEvent.getHubId())
                .setTimestamp(sensorEvent.getTimestamp())
                .setPayload(payload)
                .build();

        log.info("Записываем событие датчика: {}", message);

        ProducerRecord<String, SpecificRecordBase> record =
                new ProducerRecord<>(CollectorTopics.TELEMETRY_SENSORS, message);

        producer.send(record);
    }

    public void collectHubEvent(HubEvent hubEvent) {
        Object payload = null;
        switch (hubEvent.getType()) {
            case DEVICE_ADDED -> {
                DeviceAddedEvent event = (DeviceAddedEvent) hubEvent;
                payload = new DeviceAddedEventAvro(event.getId(), AvroMapper.mapToDeviceTypeAvro(event.getDeviceType()));
            }
            case DEVICE_REMOVED -> {
                DeviceRemovedEvent event = (DeviceRemovedEvent) hubEvent;
                payload = new DeviceRemovedEventAvro(event.getId());
            }
            case SCENARIO_ADDED -> {
                ScenarioAddedEvent event = (ScenarioAddedEvent) hubEvent;
                payload = new ScenarioAddedEventAvro(event.getName(),
                        AvroMapper.mapToConditionTypeAvro(event.getConditions()),
                        AvroMapper.mapToDeviceActionAvro(event.getActions()));
            }
            case SCENARIO_REMOVED -> {
                ScenarioRemovedEvent event = (ScenarioRemovedEvent) hubEvent;
                payload = new ScenarioRemovedEventAvro(event.getName());
            }
        }

        HubEventAvro message = HubEventAvro.newBuilder()
                .setHubId(hubEvent.getHubId())
                .setTimestamp(hubEvent.getTimestamp())
                .setPayload(payload)
                .build();

        log.info("Записываем событие хаба: {}", message);

        ProducerRecord<String, SpecificRecordBase> record =
                new ProducerRecord<>(CollectorTopics.TELEMETRY_HUBS, message);

        producer.send(record);
    }
}
