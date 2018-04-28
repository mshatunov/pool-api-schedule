package com.mshatunov.pool.api.schedule.service;

import com.mshatunov.pool.api.schedule.configuration.ScheduleApplicationProperties;
import com.mshatunov.pool.api.schedule.controller.dto.CustomerTrainingDTO;
import com.mshatunov.pool.api.schedule.controller.dto.NewTrainingRequest;
import com.mshatunov.pool.api.schedule.exception.TrainingNotBelongToCustomerException;
import com.mshatunov.pool.api.schedule.repository.ScheduleRepository;
import com.mshatunov.pool.api.schedule.repository.model.Training;
import com.mshatunov.pool.api.schedule.service.converter.TrainingConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomerScheduleServiceImpl implements CustomerScheduleService {

    private final ScheduleRepository repository;
    private final TrainingConverter converter;
    private final ScheduleApplicationProperties properties;

    @Override
    public List<CustomerTrainingDTO> getCustomerTrainings(String customerId, boolean showOnlyFutureTrainings) {
        LocalDateTime now = LocalDateTime.now();
        return repository.findByCustomerId(customerId).stream()
                .filter(tr -> !showOnlyFutureTrainings || tr.getStart().plus(tr.getDuration()).isAfter(now))
                .map(converter::trainingToCustomerTrainingsDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerTrainingDTO addCustomerTraining(String customerId, NewTrainingRequest request) {
        Training training = Training.builder()
                .customerId(customerId)
                .poolId(request.getPoolId())
                .teacherId(request.getTeacherId())
                .start(request.getStart())
                .duration(Duration.ofMinutes(properties.getDuration()))
                .build();
        return converter.trainingToCustomerTrainingsDTO(repository.insert(training));
    }

    @Override
    public void deleteCustomerTraining(String customerId, String trainingId) {
        Optional<Training> training = repository.findById(trainingId);
        if (training.isPresent() && customerId.equals(training.get().getCustomerId())) {
            repository.deleteById(trainingId);
        } else {
            throw new TrainingNotBelongToCustomerException(customerId, trainingId);
        }
    }
}
