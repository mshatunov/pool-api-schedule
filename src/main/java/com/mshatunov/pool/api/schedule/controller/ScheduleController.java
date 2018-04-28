package com.mshatunov.pool.api.schedule.controller;

import com.mshatunov.pool.api.schedule.controller.dto.CustomerTrainingDTO;
import com.mshatunov.pool.api.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
rename import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ScheduleController {

    public static final String CUSTOMER_ID = "{customerId}";
    public static final String CUSTOMER_PATH = "customerId";

    private final ScheduleService service;

    @GetMapping(value = CUSTOMER_ID)
    public List<CustomerTrainingDTO> getCustomerTrainings(@PathVariable(CUSTOMER_PATH) String customerId,
                                                          @RequestParam boolean showOnlyFutureTrainings) {
        return service.getCustomerTrainings(customerId, showOnlyFutureTrainings);
    }
}
