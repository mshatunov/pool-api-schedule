package com.mshatunov.pool.api.schedule.controller;

import com.mshatunov.pool.api.schedule.controller.dto.CustomerTrainingDTO;
import com.mshatunov.pool.api.schedule.service.PoolScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.mshatunov.pool.api.schedule.controller.PoolScheduleController.POOL_ID;

@RestController
@RequestMapping(POOL_ID)
@RequiredArgsConstructor
public class PoolScheduleController {

    public static final String POOL_ID = "pool/{poolId}";
    public static final String POOL_PATH = "poolId";

    private final PoolScheduleService service;

    @GetMapping
    public List<CustomerTrainingDTO> getPoolTrainings(@PathVariable(POOL_PATH) String poolId,
                                                      @RequestParam boolean showOnlyFutureTrainings) {
        return service.getPoolTrainings(poolId, showOnlyFutureTrainings);
    }
}
