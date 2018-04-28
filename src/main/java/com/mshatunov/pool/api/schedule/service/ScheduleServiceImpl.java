package com.mshatunov.pool.api.schedule.service;

import com.mshatunov.pool.api.schedule.controller.dto.CustomerTrainingDTO;
import com.mshatunov.pool.api.schedule.repository.ScheduleRepository;
import com.mshatunov.pool.api.schedule.service.converter.TrainingConverter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository repository;
    private final TrainingConverter converter;

    @Override
    public List<CustomerTrainingDTO> getCustomerClasses(String customerId) {
        return repository.findByCustomerId(customerId).stream()
                .map(converter::trainingToCustomerTrainingsDTO)
                .collect(Collectors.toList());
    }
}
