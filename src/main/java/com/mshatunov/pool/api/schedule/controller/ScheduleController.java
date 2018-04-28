package com.mshatunov.pool.api.schedule.controller;

import com.mshatunov.pool.api.schedule.controller.dto.CustomerTrainingDTO;
import com.mshatunov.pool.api.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ScheduleController {

    public static final String CUSTOMER_ID = "{customerId}";
    public static final String CUSTOMER_PATH = "customerId";

    private final ScheduleService service;

    @GetMapping(value = CUSTOMER_ID)
    public List<CustomerTrainingDTO> getCustomerClasses(@PathVariable(CUSTOMER_PATH) String customerId) {
        return service.getCustomerClasses(customerId);
    }
}
