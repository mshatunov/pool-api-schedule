package com.mshatunov.pool.api.schedule.service;

import com.mshatunov.pool.api.schedule.clent.InstructorClient;
import com.mshatunov.pool.api.schedule.configuration.ScheduleApplicationProperties;
import com.mshatunov.pool.api.schedule.controller.dto.CustomerTrainingDTO;
import com.mshatunov.pool.api.schedule.repository.ScheduleRepository;
import com.mshatunov.pool.api.schedule.repository.model.Training;
import com.mshatunov.pool.api.schedule.service.converter.TrainingConverter;
import com.mshatunov.pool.api.schedule.service.utils.ScheduleUtilsService;
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
    private final ScheduleUtilsService utilsService;
    private final InstructorClient instructorClient;
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
    public List<CustomerTrainingDTO> getAvailablePoolTrainings(String poolId, String tubId, LocalDate start, LocalDate finish) {
        Map<LocalDateTime, Training> bookedTrainings = repository.findByPoolIdAndTubIdAndStartBetween(
                poolId, tubId, LocalDateTime.of(start, LocalTime.MIN), LocalDateTime.of(finish, LocalTime.MAX)).stream()
                .collect(Collectors.toMap(Training::getStart, Function.identity()));

        return searchAvailableTrainings(poolId, tubId, start, finish, bookedTrainings);
    }

    private List<CustomerTrainingDTO> searchAvailableTrainings(String poolId, String tubId, LocalDate start, LocalDate finish,
                                                               Map<LocalDateTime, Training> bookedTrainings) {
        List<CustomerTrainingDTO> availableTrainings = new ArrayList<>();

        for (LocalDate date = start; date.isBefore(finish); date = date.plusDays(1)) {
            LocalDate processingDate = date;
            utilsService.getOpenTimes(date).stream()
                    .map(time -> LocalDateTime.of(processingDate, time))
                    .filter(dt -> !bookedTrainings.containsKey(dt))
                    .forEach(dt -> availableTrainings.add(CustomerTrainingDTO.builder()
                            .poolId(poolId)
                            .tubId(tubId)
                            .start(dt)
                            .end(dt.plusMinutes(properties.getDuration()))
                            .build()));
        }
        return availableTrainings;
    }

    private List<CustomerTrainingDTO> filterAndConvertTrainings(Stream<Training> trainingsStream, boolean showOnlyFutureTrainings) {
        LocalDateTime now = LocalDateTime.now();
        return trainingsStream
                .filter(tr -> !showOnlyFutureTrainings || tr.getEnd().isAfter(now))
                .sorted(Comparator.comparing(Training::getStart))
                .map(tr -> converter.trainingToCustomerTrainingsDTO(tr,
                        instructorClient.getInstructorByPoolAndDate(tr.getPoolId(), tr.getTubId(), tr.getStart().toLocalDate().toString())))
                .collect(Collectors.toList());
    }
}
