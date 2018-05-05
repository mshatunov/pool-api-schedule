package com.mshatunov.pool.api.schedule.clent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Instructor {
    private String id;
    private String name;
    private String lastName;
}
