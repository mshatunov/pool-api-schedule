package com.mshatunov.pool.api.schedule.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerTrainingDTO {
    private String id;
    private String teacherId;
    private String poolId;
    private LocalDateTime start;
    private Duration duration;
}
