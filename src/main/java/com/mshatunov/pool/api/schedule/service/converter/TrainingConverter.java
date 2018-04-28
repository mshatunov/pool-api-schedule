package com.mshatunov.pool.api.schedule.service.converter;

import com.mshatunov.pool.api.schedule.controller.dto.CustomerTrainingDTO;
import com.mshatunov.pool.api.schedule.repository.model.Training;
import org.mapstruct.Mapper;

@Mapper
public interface TrainingConverter {
    CustomerTrainingDTO trainingToCustomerTrainingsDTO(Training training);
}
