package ru.practicum.analyzer.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.practicum.analyzer.enums.HubEventType;
import ru.practicum.analyzer.handlers.HubEventHandler;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
@RequiredArgsConstructor
public class HubEventHandlersConfiguration {
    private final Set<HubEventHandler> handlers;

    @Bean
    public Map<HubEventType, HubEventHandler> getHandlers() {
        return handlers.stream()
                .collect(Collectors.toMap(HubEventHandler::getPayloadType, Function.identity()));
    }
}
