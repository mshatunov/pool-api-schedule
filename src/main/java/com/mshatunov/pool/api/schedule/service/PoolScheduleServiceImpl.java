package com.mshatunov.pool.api.schedule.service;

import com.mshatunov.pool.api.schedule.controller.dto.CustomerTrainingDTO;
import com.mshatunov.pool.api.schedule.repository.ScheduleRepository;
import com.mshatunov.pool.api.schedule.repository.model.Training;
import com.mshatunov.pool.api.schedule.service.converter.TrainingConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PoolScheduleServiceImpl implements PoolScheduleService {

    private final ScheduleRepository repository;
    private final TrainingConverter converter;

    @Override
    public List<CustomerTrainingDTO> getPoolTrainings(String poolId, boolean showOnlyFutureTrainings) {
        return filterAndConvertTrainings(repository.findByPoolId(poolId).stream(), showOnlyFutureTrainings);
    }

    @Override
    public List<CustomerTrainingDTO> getTubTrainings(String poolId, String tubId, boolean showOnlyFutureTrainings) {
        return filterAndConvertTrainings(repository.findByPoolIdAndTubId(poolId, tubId).stream(), showOnlyFutureTrainings);
    }

    private List<CustomerTrainingDTO> filterAndConvertTrainings(Stream<Training> trainingsStream, boolean showOnlyFutureTrainings) {
        LocalDateTime now = LocalDateTime.now();
        return trainingsStream
                .filter(tr -> !showOnlyFutureTrainings || tr.getEnd().isAfter(now))
                .map(converter::trainingToCustomerTrainingsDTO)
                .collect(Collectors.toList());
    }
}
