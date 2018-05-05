package com.mshatunov.pool.api.schedule.service.converter;

import com.mshatunov.pool.api.schedule.clent.dto.Instructor;
import com.mshatunov.pool.api.schedule.controller.dto.CustomerTrainingDTO;
import com.mshatunov.pool.api.schedule.controller.dto.NewTrainingRequest;
import com.mshatunov.pool.api.schedule.repository.model.Training;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TrainingConverter {
    @Mapping(target = "id", source = "training.id")
    @Mapping(target = "instructor", source = "instructor")
    CustomerTrainingDTO trainingToCustomerTrainingsDTO(Training training, Instructor instructor);
    Training newTrainingRequesttoTraining(NewTrainingRequest request);
}
