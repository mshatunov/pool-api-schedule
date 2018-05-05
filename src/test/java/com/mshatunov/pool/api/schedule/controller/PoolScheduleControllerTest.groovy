package com.mshatunov.pool.api.schedule.controller

import com.mshatunov.pool.api.schedule.BaseIntegrationTest
import com.mshatunov.pool.api.schedule.repository.model.Training
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

import static org.junit.jupiter.api.Assertions.*

class PoolScheduleControllerTest extends BaseIntegrationTest {

    @Autowired
    PoolScheduleController controller

    @Test
    void 'successfully get empty pool trainings'() {
        def response = controller.getPoolTrainings(POOL_1, false)
        assertEquals(Collections.emptyList(), response)
    }

    @Test
    void 'successfully get all pool trainings'() {
        repository.insert(TRAINING_1)
        repository.insert(TRAINING_2)
        repository.insert(TRAINING_3)
        def response = controller.getPoolTrainings(POOL_1, false)
        assertEquals(2, response.size())
    }

    @Test
    void 'successfully get only future pool trainings'() {
        repository.insert(TRAINING_1)
        repository.insert(TRAINING_2)
        repository.insert(TRAINING_3)
        def response = controller.getPoolTrainings(POOL_1, true)
        assertEquals(1, response.size())
        assertEquals('training_2', response.get(0).getId())
        assertEquals(TUB_2, response.get(0).getTubId())
        assertEquals(TIME_2, response.get(0).getStart())
        assertEquals(TIME_2.plusMinutes(properties.getDuration()), response.get(0).getEnd())
        assertEquals(INSTRUCTOR_ID, response.get(0).getInstructor().getId())
        assertEquals(INSTRUCTOR_NAME, response.get(0).getInstructor().getName())
        assertEquals(INSTRUCTOR_LAST_NAME, response.get(0).getInstructor().getLastName())
    }

    @Test
    void 'successfully get all pod trainings'() {
        repository.insert(TRAINING_1)
        repository.insert(TRAINING_2)
        repository.insert(TRAINING_3)
        def response = controller.getTubTrainings(POOL_1, TUB_1, false)
        assertEquals(1, response.size())
        assertEquals('training_1', response.get(0).getId())
    }

    @Test
    void 'successfully get all available tub trainings'() {
        def time = LocalDateTime.of(LocalDate.now().plusDays(1), LocalTime.of(11, 00))
        repository.insert(Training.builder()
                .id('training_1')
                .customerId(CUSTOMER)
                .poolId(POOL_1)
                .tubId(TUB_1)
                .start(time)
                .end(time.plusMinutes(properties.getDuration()))
                .build())
        repository.insert(Training.builder()
                .id('training_2')
                .customerId(CUSTOMER)
                .poolId(POOL_1)
                .tubId(TUB_1)
                .start(time.plusMinutes(60))
                .end(time.plusMinutes(60 + properties.getDuration()))
                .build())

        def response = controller.getAvailableTubTrainings(POOL_1, TUB_1, 2)
                .collectEntries { [it.start, it] }

        assertFalse(response.containsKey(time))
        assertFalse(response.containsKey(time.plusMinutes(60)))

        assertTrue(response.containsKey(time.plusMinutes(30)))
        assertTrue(response.containsKey(time.plusMinutes(90)))
    }

}
