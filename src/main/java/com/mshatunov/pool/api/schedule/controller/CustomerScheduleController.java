package com.mshatunov.pool.api.schedule.controller;

import com.mshatunov.pool.api.schedule.controller.dto.CustomerTrainingDTO;
import com.mshatunov.pool.api.schedule.controller.dto.NewTrainingRequest;
import com.mshatunov.pool.api.schedule.service.CustomerScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.mshatunov.pool.api.schedule.controller.CustomerScheduleController.CUSTOMER_ID;

@RestController
@RequestMapping(CUSTOMER_ID)
@RequiredArgsConstructor
public class CustomerScheduleController {

    public static final String CUSTOMER_ID = "customer/{customerId}";
    public static final String CUSTOMER_PATH = "customerId";

    public static final String TRAINING_ID = "/{trainingId}";
    public static final String TRAINING_PATH = "trainingId";

    private final CustomerScheduleService service;

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

    @DeleteMapping(TRAINING_ID)
    public void deleteCustomerTraining(@PathVariable(CUSTOMER_PATH) String customerId,
                                       @PathVariable(TRAINING_PATH) String trainingId) {
        service.deleteCustomerTraining(customerId, trainingId);
    }
}
