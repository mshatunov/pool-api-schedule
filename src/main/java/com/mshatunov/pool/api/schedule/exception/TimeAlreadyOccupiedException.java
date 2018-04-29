package com.mshatunov.pool.api.schedule.exception;

import java.time.LocalDateTime;

public class TimeAlreadyOccupiedException extends ScheduleException {
    public TimeAlreadyOccupiedException(String poolId, String tubId, LocalDateTime time) {
        super(String.format("Time %s in pool %s in tub %s is already occupied", time.toString(), poolId, tubId));
    }
}
