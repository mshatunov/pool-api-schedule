package com.mshatunov.pool.api.schedule.repository.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "schedule")
public class Training {
    @Id
    private String id;
    @Indexed
    private String customerId;
    @Indexed
    private String teacherId;
    @Indexed
    private String poolId;
    @Indexed
    private LocalDateTime start;
    private Duration duration;
}
