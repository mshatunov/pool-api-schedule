package com.mshatunov.pool.api.schedule.repository;

import com.mshatunov.pool.api.schedule.repository.model.Training;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ScheduleRepository extends MongoRepository<Training, String> {
    List<Training> findByCustomerId(String customerId);
}
