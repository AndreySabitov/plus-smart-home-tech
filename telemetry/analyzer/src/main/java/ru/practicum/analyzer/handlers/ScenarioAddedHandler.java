package ru.practicum.analyzer.handlers;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.analyzer.enums.HubEventType;
import ru.practicum.analyzer.model.Action;
import ru.practicum.analyzer.model.Condition;
import ru.practicum.analyzer.model.Scenario;
import ru.practicum.analyzer.repository.ActionRepository;
import ru.practicum.analyzer.repository.ConditionRepository;
import ru.practicum.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ScenarioAddedHandler implements HubEventHandler {
    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;

    @Override
    @Transactional
    public void handle(HubEventAvro event) {
        ScenarioAddedEventAvro scenarioAddedEvent = (ScenarioAddedEventAvro) event.getPayload();

        scenarioRepository.save(mapToScenario(event));
        conditionRepository.saveAll(mapToCondition(scenarioAddedEvent));
        actionRepository.saveAll(mapToAction(scenarioAddedEvent));
    }

    @Override
    public HubEventType getPayloadType() {
        return HubEventType.SCENARIO_ADDED;
    }

    private Scenario mapToScenario(HubEventAvro event) {
        ScenarioAddedEventAvro scenarioAddedEvent = (ScenarioAddedEventAvro) event.getPayload();

        return Scenario.builder()
                .name(scenarioAddedEvent.getName())
                .hubId(event.getHubId())
                .build();
    }

    private List<Condition> mapToCondition(ScenarioAddedEventAvro scenarioAddedEvent) {
        return scenarioAddedEvent.getConditions().stream()
                .map(c -> Condition.builder()
                        .type(c.getType())
                        .operation(c.getOperation())
                        .value((Integer) c.getValue()) // тут может быть boolean
                        .build())
                .toList();
    }

    private List<Action> mapToAction(ScenarioAddedEventAvro scenarioAddedEvent) {
        return scenarioAddedEvent.getActions().stream()
                .map(action -> Action.builder()
                        .type(action.getType())
                        .value(action.getValue())
                        .build())
                .toList();
    }
}
