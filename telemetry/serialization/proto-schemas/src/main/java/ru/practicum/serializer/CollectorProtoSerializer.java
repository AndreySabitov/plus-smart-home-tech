package ru.practicum.serializer;

import com.google.protobuf.GeneratedMessageV3;
import org.apache.kafka.common.serialization.Serializer;

public class CollectorProtoSerializer implements Serializer<GeneratedMessageV3> {

    @Override
    public byte[] serialize(String topic, GeneratedMessageV3 data) {
        return data.toByteArray();
    }
}
