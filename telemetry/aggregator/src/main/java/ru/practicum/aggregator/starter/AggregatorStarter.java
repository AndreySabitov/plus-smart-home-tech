package ru.practicum.aggregator.starter;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ru.practicum.aggregator.handler.SensorEventHandler;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregatorStarter {
    private final KafkaConsumer<String, SpecificRecordBase> consumer;
    private final SensorEventHandler eventHandler;
    private final Producer<String, SpecificRecordBase> producer;
    @Value("${aggregator.topic.telemetry-snapshots}")
    private String topic;

    public void start() {
        try {
            consumer.subscribe(List.of("telemetry.sensors.v1"));

            Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));

            while (true) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(Duration.ofMillis(1000));

                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    log.info("обрабатываем сообщение {}", record.value());
                    SensorEventAvro event = (SensorEventAvro) record.value();
                    Optional<SensorsSnapshotAvro> snapshot = eventHandler.updateState(event);
                    log.info("Получили снимок состояния {}", snapshot);
                    if (snapshot.isPresent()) {
                        log.info("запись снимка в топик Kafka");
                        ProducerRecord<String, SpecificRecordBase> message = new ProducerRecord<>(topic, null,
                                event.getTimestamp().toEpochMilli(), event.getHubId(), snapshot.get());

                        producer.send(message);
                        producer.flush();
                    }
                }
                consumer.commitSync();
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("Ошибка во время обработки событий от датчиков", e);
        } finally {
            try {
                consumer.commitSync();
                producer.flush();
            } finally {
                log.info("Закрываем консьюмер");
                consumer.close(Duration.ofSeconds(10));
                producer.close(Duration.ofSeconds(10));
            }
        }
    }
}
