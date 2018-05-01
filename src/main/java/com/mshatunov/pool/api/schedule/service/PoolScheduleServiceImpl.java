package com.mshatunov.pool.api.schedule.service;

import com.mshatunov.pool.api.schedule.configuration.ScheduleApplicationProperties;
import com.mshatunov.pool.api.schedule.controller.dto.CustomerTrainingDTO;
import com.mshatunov.pool.api.schedule.repository.ScheduleRepository;
import com.mshatunov.pool.api.schedule.repository.model.Training;
import com.mshatunov.pool.api.schedule.service.converter.TrainingConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class PoolScheduleServiceImpl implements PoolScheduleService {

    private final ScheduleRepository repository;
    private final TrainingConverter converter;
    private final ScheduleApplicationProperties properties;

    @Override
    public List<CustomerTrainingDTO> getPoolTrainings(String poolId, boolean showOnlyFutureTrainings) {
        return filterAndConvertTrainings(repository.findByPoolId(poolId).stream(), showOnlyFutureTrainings);
    }

    @Override
    public List<CustomerTrainingDTO> getTubTrainings(String poolId, String tubId, boolean showOnlyFutureTrainings) {
        return filterAndConvertTrainings(repository.findByPoolIdAndTubId(poolId, tubId).stream(), showOnlyFutureTrainings);
    }

    @Override
    public List<CustomerTrainingDTO> getAvailablePoolTrainings(String poolId, String tubId, Integer duration) {
        Map<LocalDateTime, Training> bookedTrainings = repository.findByPoolIdAndTubIdAndStartBetween(
                poolId, tubId, LocalDateTime.now(), LocalDateTime.now().plusDays(duration)).stream()
                .collect(Collectors.toMap(Training::getStart, Function.identity()));

        return searchAvailableTrainings(poolId, tubId, duration, bookedTrainings);
    }

    private List<CustomerTrainingDTO> searchAvailableTrainings(String poolId, String tubId, Integer duration, Map<LocalDateTime, Training> bookedTrainings) {
        LocalDate now = LocalDate.now();
        List<CustomerTrainingDTO> availableTrainings = new ArrayList<>();

        for (LocalDate date = now; date.isBefore(now.plusDays(duration)); date = date.plusDays(1)) {
            for (LocalTime time = date.equals(now) ? LocalTime.now() : LocalTime.parse(properties.getTimeOpen());
                 time.isBefore(LocalTime.parse(properties.getTimeClose()));
                 time = time.plusMinutes(properties.getDuration())) {

                LocalDateTime processingTime = LocalDateTime.of(date, time);
                if (!bookedTrainings.containsKey(processingTime)) {
                    availableTrainings.add(CustomerTrainingDTO.builder()
                            .poolId(poolId)
                            .tubId(tubId)
                            .start(processingTime)
                            .end(processingTime.plusMinutes(properties.getDuration()))
                            .build());
                }
            }
        }
        return availableTrainings;
    }

    private List<CustomerTrainingDTO> filterAndConvertTrainings(Stream<Training> trainingsStream, boolean showOnlyFutureTrainings) {
        LocalDateTime now = LocalDateTime.now();
        return trainingsStream
                .filter(tr -> !showOnlyFutureTrainings || tr.getEnd().isAfter(now))
                .sorted(Comparator.comparing(Training::getStart))
                .map(converter::trainingToCustomerTrainingsDTO)
                .collect(Collectors.toList());
    }
}
