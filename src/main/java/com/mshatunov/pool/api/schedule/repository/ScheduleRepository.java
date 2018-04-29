package com.mshatunov.pool.api.schedule.repository;

import com.mshatunov.pool.api.schedule.repository.model.Training;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends MongoRepository<Training, String> {
    List<Training> findByCustomerId(String customerId);
    List<Training> findByPoolId(String poolId);
    List<Training> findByPoolIdAndTubId(String poolId, String tubId);
    List<Training> findByPoolIdAndTubIdAndStartBetween(String poolId, String tubId, LocalDateTime time1, LocalDateTime time2);
}
