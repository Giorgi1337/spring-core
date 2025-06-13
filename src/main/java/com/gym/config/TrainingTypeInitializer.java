package com.gym.config;

import com.gym.model.TrainingType;
import com.gym.service.TrainingTypeService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Transactional
public class TrainingTypeInitializer {

    private static final Logger logger = LogManager.getLogger(TrainingTypeInitializer.class);

    private final TrainingTypeService trainingTypeService;

    @PostConstruct
    public void initTrainingTypes() {
        List<String> defaultTypes = List.of("Cardio", "Strength", "Yoga", "Zumba", "Boxing");
        for (String type : defaultTypes) {
            try {
                if (trainingTypeService.getTrainingTypeByName(type).isEmpty()) {
                    TrainingType trainingType = TrainingType.builder()
                            .trainingTypeName(type)
                            .build();
                    trainingTypeService.createTrainingType(trainingType);
                    logger.info("Created training type: {}", type);
                }
                logger.debug("Training type already exists: {}", type);
            } catch (Exception e) {
                logger.warn("Failed to create training type '{}': {}", type, e.getMessage());
            }
        }
    }
}