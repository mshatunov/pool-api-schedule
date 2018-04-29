package com.mshatunov.pool.api.schedule.service;

import com.mshatunov.pool.api.schedule.configuration.ScheduleApplicationProperties;
import com.mshatunov.pool.api.schedule.controller.dto.CustomerTrainingDTO;
import com.mshatunov.pool.api.schedule.controller.dto.NewTrainingRequest;
import com.mshatunov.pool.api.schedule.exception.TimeAlreadyOccupiedException;
import com.mshatunov.pool.api.schedule.exception.TrainingNotBelongToCustomerException;
import com.mshatunov.pool.api.schedule.repository.ScheduleRepository;
import com.mshatunov.pool.api.schedule.repository.model.Training;
import com.mshatunov.pool.api.schedule.service.converter.TrainingConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
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
                .filter(tr -> !showOnlyFutureTrainings || tr.getEnd().isAfter(now))
                .sorted(Comparator.comparing(Training::getStart))
                .map(converter::trainingToCustomerTrainingsDTO)
                .collect(Collectors.toList());
    }

    @Override
    public CustomerTrainingDTO addCustomerTraining(String customerId, NewTrainingRequest request) {
        if (timeIsUnavailable(request.getPoolId(), request.getTubId(), request.getStart())) {
            throw new TimeAlreadyOccupiedException(request.getPoolId(), request.getTubId(), request.getStart());
        }

        Training training = converter.newTrainingRequesttoTraining(request)
                .setCustomerId(customerId)
                .setEnd(request.getStart().plusMinutes(properties.getDuration()));
        return converter.trainingToCustomerTrainingsDTO(repository.insert(training));
    }

    @Override
    public void deleteCustomerTraining(String customerId, String trainingId) {
        repository.findById(trainingId)
                .filter(tr -> customerId.equals(tr.getCustomerId()))
                .orElseThrow(() -> new TrainingNotBelongToCustomerException(customerId, trainingId));
        repository.deleteById(trainingId);
    }

    private boolean timeIsUnavailable(String poolId, String tubId, LocalDateTime startTime) {
        LocalDateTime endTime = startTime.plusMinutes(properties.getDuration());
        return repository.findByPoolIdAndTubIdAndStartBetween(poolId, tubId,
                startTime.minusMinutes(properties.getDuration()), endTime)
                .stream()
                .anyMatch(tr -> tr.getStart().isEqual(startTime) ||
                        tr.getStart().isBefore(startTime) && tr.getEnd().isAfter(startTime) ||
                        tr.getStart().isBefore(endTime) && tr.getEnd().isAfter(endTime));
    }
}
