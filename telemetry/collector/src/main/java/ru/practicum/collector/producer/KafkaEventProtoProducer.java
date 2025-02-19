package ru.practicum.collector.producer;

import com.google.protobuf.GeneratedMessageV3;
import lombok.RequiredArgsConstructor;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;

@Component
@RequiredArgsConstructor
public class KafkaEventProtoProducer implements AutoCloseable {
    private final Producer<String, GeneratedMessageV3> producerProto;

    public void send(GeneratedMessageV3 message, String hubId, Instant timestamp, String topic) {
        ProducerRecord<String, GeneratedMessageV3> record = new ProducerRecord<>(topic, null,
                timestamp.toEpochMilli(), hubId, message);

        producerProto.send(record);
        producerProto.flush();
    }

    @Override
    public void close() {
        producerProto.flush();
        producerProto.close(Duration.ofSeconds(10));
    }
}
