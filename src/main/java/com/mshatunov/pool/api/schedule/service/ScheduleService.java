package com.mshatunov.pool.api.schedule.service;

import com.mshatunov.pool.api.schedule.controller.dto.CustomerTrainingDTO;
import com.mshatunov.pool.api.schedule.controller.dto.NewTrainingRequest;

import java.util.List;

public interface ScheduleService {
    List<CustomerTrainingDTO> getCustomerTrainings(String customerId, boolean showOnlyFutureTrainings);
    CustomerTrainingDTO addCustomerTraining(String customerId, NewTrainingRequest trainingRequest);
}
