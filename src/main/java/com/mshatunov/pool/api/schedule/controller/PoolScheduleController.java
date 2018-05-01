package com.mshatunov.pool.api.schedule.controller;

import com.mshatunov.pool.api.schedule.configuration.ScheduleApplicationProperties;
import com.mshatunov.pool.api.schedule.controller.dto.CustomerTrainingDTO;
import com.mshatunov.pool.api.schedule.service.PoolScheduleService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import static com.mshatunov.pool.api.schedule.controller.PoolScheduleController.POOL_ID;

@RestController
@RequestMapping(POOL_ID)
@RequiredArgsConstructor
public class PoolScheduleController {

    public static final String POOL_ID = "pool/{poolId}";
    public static final String POOL_PATH = "poolId";

    public static final String TUB_ID = "/{tubId}";
    public static final String TUB_PATH = "tubId";

    private final PoolScheduleService service;
    private final ScheduleApplicationProperties properties;

    @GetMapping
    public List<CustomerTrainingDTO> getPoolTrainings(@PathVariable(POOL_PATH) String poolId,
                                                      @RequestParam boolean showOnlyFutureTrainings) {
        return service.getPoolTrainings(poolId, showOnlyFutureTrainings);
    }

    @GetMapping(TUB_ID)
    public List<CustomerTrainingDTO> getTubTrainings(@PathVariable(POOL_PATH) String poolId,
                                                     @PathVariable(TUB_PATH) String tubId,
                                                     @RequestParam boolean showOnlyFutureTrainings) {
        return service.getTubTrainings(poolId, tubId, showOnlyFutureTrainings);
    }

    @GetMapping(TUB_ID + "/available")
    public List<CustomerTrainingDTO> getAvailableTubTrainings(@PathVariable(POOL_PATH) String poolId,
                                                              @PathVariable(TUB_PATH) String tubId,
                                                              @RequestParam Integer depth) {
        return service.getAvailablePoolTrainings(poolId, tubId, Optional.ofNullable(depth).orElse(properties.getDefaultDepth()));
    }
}
