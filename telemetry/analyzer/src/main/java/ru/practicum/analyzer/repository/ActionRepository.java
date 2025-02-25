package ru.practicum.analyzer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.analyzer.model.Action;
import ru.practicum.analyzer.model.Scenario;

public interface ActionRepository extends JpaRepository<Action, Long> {
    void deleteByScenario(Scenario scenario);
}
