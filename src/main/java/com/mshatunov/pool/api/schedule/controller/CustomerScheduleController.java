package com.mshatunov.pool.api.schedule.controller;

import com.mshatunov.pool.api.schedule.controller.dto.CustomerTrainingDTO;
import com.mshatunov.pool.api.schedule.controller.dto.NewTrainingRequest;
import com.mshatunov.pool.api.schedule.service.ScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.mshatunov.pool.api.schedule.controller.CustomerScheduleController.CUSTOMER_ID;

@RestController(value = CUSTOMER_ID)
@RequiredArgsConstructor
public class CustomerScheduleController {

    public static final String CUSTOMER_ID = "{customerId}";
    public static final String CUSTOMER_PATH = "customerId";

    private final ScheduleService service;

    @GetMapping
    public List<CustomerTrainingDTO> getCustomerTrainings(@PathVariable(CUSTOMER_PATH) String customerId,
                                                          @RequestParam boolean showOnlyFutureTrainings) {
        return service.getCustomerTrainings(customerId, showOnlyFutureTrainings);
    }

    @PostMapping
    public CustomerTrainingDTO addCustomerTraining(@PathVariable(CUSTOMER_PATH) String customerId,
                                                   @RequestBody NewTrainingRequest trainingRequest) {
        return service.addCustomerTraining(customerId, trainingRequest);
    }
}
