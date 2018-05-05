package com.mshatunov.pool.api.schedule.controller.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NewTrainingRequest {
    private String poolId;
    private String tubId;
    private LocalDateTime start;
}
