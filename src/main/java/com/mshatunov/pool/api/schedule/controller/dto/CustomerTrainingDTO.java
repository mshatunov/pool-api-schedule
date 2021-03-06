package com.mshatunov.pool.api.schedule.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CustomerTrainingDTO {
    private String id;
    private InstructorDTO instructor;
    private String poolId;
    private String tubId;
    private LocalDateTime start;
    private LocalDateTime end;
}
