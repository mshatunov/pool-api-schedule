package com.mshatunov.pool.api.schedule.service.utils;

import com.mshatunov.pool.api.schedule.configuration.ScheduleApplicationProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ScheduleUtilsServiceImpl implements ScheduleUtilsService {

    private final ScheduleApplicationProperties properties;


    @Override
    public List<LocalTime> getOpenTimes(LocalDate date) {
        LocalDate now = LocalDate.now();
        List<LocalTime> openedTimes = new ArrayList<>();
        for (LocalTime time = date.equals(now) ? LocalTime.now() : LocalTime.parse(properties.getTimeOpen());
             time.isBefore(LocalTime.parse(properties.getTimeClose()));
             time = time.plusMinutes(properties.getDuration())) {
            openedTimes.add(time);
        }
        return openedTimes;
    }
}
