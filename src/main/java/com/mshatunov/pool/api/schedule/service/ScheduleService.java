package com.mshatunov.pool.api.schedule.service;

import com.mshatunov.pool.api.schedule.controller.dto.CustomerTrainingDTO;

import java.util.List;

public interface ScheduleService {
    List<CustomerTrainingDTO> getCustomerTrainings(String customerId, boolean showOnlyFutureTrainings);
}
