package com.mshatunov.pool.api.schedule.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotNull;

@Data
@Validated
@ConfigurationProperties(prefix = "common.params")
public class ScheduleApplicationProperties {
    @NotNull
    private Integer duration;
}
