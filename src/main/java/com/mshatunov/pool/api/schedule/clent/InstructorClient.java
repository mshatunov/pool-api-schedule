package com.mshatunov.pool.api.schedule.clent;

import com.mshatunov.pool.api.schedule.clent.dto.Instructor;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "instructor-api", url = "localhost:8080", fallbackFactory = InstructorClient.InstructorClientFallbackFactory.class)
public interface InstructorClient {
    String POOL_PATH = "poolId";
    String TUB_PATH = "tubId";
    String DATE_PATH = "date";

    @GetMapping("/schedule/{poolId}/{tubId}/{date}")
    Instructor getInstructorByPoolAndDate(@PathVariable(POOL_PATH) String poolId,
                                          @PathVariable(TUB_PATH) String tubId,
                                          @PathVariable(DATE_PATH) String date);

    @Slf4j
    @Component
    class InstructorClientFallbackFactory implements FallbackFactory<InstructorClient> {
        @Override
        public InstructorClient create(Throwable cause) {
            return (poolId, tubId, date) -> Instructor.builder().build();
        }
    }

}
