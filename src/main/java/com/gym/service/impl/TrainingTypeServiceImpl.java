package com.gym.service.impl;

import com.gym.dao.TrainingTypeDao;
import com.gym.model.TrainingType;
import com.gym.service.TrainingTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class TrainingTypeServiceImpl implements TrainingTypeService {

    private final TrainingTypeDao trainingTypeDao;

    @Override
    public TrainingType createTrainingType(TrainingType trainingType) {
        trainingTypeDao.save(trainingType);
        return trainingType;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TrainingType> getTrainingTypeById(Long id) {
        return Optional.ofNullable(trainingTypeDao.findById(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<TrainingType> getTrainingTypeByName(String name) {
        return trainingTypeDao.findByTrainingTypeName(name);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TrainingType> getAllTrainingTypes() {
        return trainingTypeDao.findAll();
    }
}
