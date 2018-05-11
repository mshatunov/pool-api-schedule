package com.mshatunov.pool.api.schedule.service.utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface ScheduleUtilsService {
    List<LocalTime> getOpenTimes(LocalDate date);
}
