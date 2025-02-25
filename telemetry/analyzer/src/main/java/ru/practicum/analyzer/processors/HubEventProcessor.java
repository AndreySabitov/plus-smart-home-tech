package ru.practicum.analyzer.processors;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.analyzer.enums.HubEventType;
import ru.practicum.analyzer.handlers.HubEventHandler;
import ru.practicum.analyzer.handlers.ScenarioRemovedHandler;
import ru.yandex.practicum.kafka.telemetry.event.*;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class HubEventProcessor implements Runnable {
    private final Consumer<String, HubEventAvro> consumer;
    private final Map<HubEventType, HubEventHandler> handlers;
    @Value("${topic.hub-event-topic}")
    private String topic;

    @Override
    public void run() {

        try {
            consumer.subscribe(List.of(topic));
            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

            while (true) {

                ConsumerRecords<String, HubEventAvro> records = consumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, HubEventAvro> record : records) {
                    HubEventAvro event = record.value();
                    String payloadName = event.getPayload().getClass().getSimpleName();
                    log.info("Получили сообщение хаба типа: {}", payloadName);
                    HubEventType type = getHubEventType(payloadName);

                    handlers.get(type).handle(event);
                }

                consumer.commitSync();
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка чтения данных из топика {}", topic);
        } finally {
            try {
                consumer.commitSync();
            } finally {
                consumer.close();
            }
        }
    }

    private HubEventType getHubEventType(String payloadName) {
        HubEventType type = null;
        if (payloadName.equals(DeviceAddedEventAvro.getClassSchema().getName())) {
            type = HubEventType.DEVICE_ADDED;
        } else if (payloadName.equals(DeviceRemovedEventAvro.getClassSchema().getName())) {
            type = HubEventType.DEVICE_REMOVED;
        } else if (payloadName.equals(ScenarioAddedEventAvro.getClassSchema().getName())) {
            type = HubEventType.SCENARIO_ADDED;
        } else if (payloadName.equals(ScenarioRemovedEventAvro.getClassSchema().getName())) {
            type = HubEventType.SCENARIO_REMOVED;
        }
        return type;
    }
}
