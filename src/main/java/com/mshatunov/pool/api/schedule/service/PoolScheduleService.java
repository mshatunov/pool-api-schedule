package com.mshatunov.pool.api.schedule.service;

import com.mshatunov.pool.api.schedule.controller.dto.CustomerTrainingDTO;

import java.time.LocalDate;
import java.util.List;

public interface PoolScheduleService {
    List<CustomerTrainingDTO> getPoolTrainings(String poolId, boolean showOnlyFutureTrainings);
    List<CustomerTrainingDTO> getTubTrainings(String poolId, String tubId, boolean showOnlyFutureTrainings);
    List<CustomerTrainingDTO> getAvailablePoolTrainings(String poolId, String tubId, LocalDate start, LocalDate finish);
}
