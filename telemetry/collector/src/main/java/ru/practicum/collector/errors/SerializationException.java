package ru.practicum.collector.errors;

public class SerializationException extends RuntimeException {
    public SerializationException(String message) {
        super(message);
    }
}
