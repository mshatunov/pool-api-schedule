package com.mshatunov.pool.api.schedule.controller

import com.mshatunov.pool.api.schedule.BaseIntegrationTest
import com.mshatunov.pool.api.schedule.configuration.ScheduleApplicationProperties
import com.mshatunov.pool.api.schedule.repository.ScheduleRepository
import com.mshatunov.pool.api.schedule.repository.model.Training
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

import static org.junit.jupiter.api.Assertions.*

class PoolScheduleControllerTest extends BaseIntegrationTest {

    public static final String CUSTOMER = 'customer_1234'
    public static final String TEACHER = 'teacher_1234'
    public static final String POOL_1 = 'pool_1234'
    public static final String POOL_2 = 'pool_5678'
    public static final String TUB_1 = '1234_left'
    public static final String TUB_2 = '1234_right'
    public static final LocalDateTime TIME_1 = LocalDateTime.of(2018, 4, 1, 10, 30)
    public static final LocalDateTime TIME_2 = LocalDateTime.of(2020, 4, 1, 10, 30)

    public static final Training TRAINING_1 = Training.builder()
            .id('training_1')
            .customerId(CUSTOMER)
            .teacherId(TEACHER)
            .poolId(POOL_1)
            .tubId(TUB_1)
            .start(TIME_1)
            .end(TIME_1.plusMinutes(30))
            .build()

    public static final Training TRAINING_2 = Training.builder()
            .id('training_2')
            .customerId(CUSTOMER)
            .teacherId(TEACHER)
            .poolId(POOL_1)
            .tubId(TUB_2)
            .start(TIME_2)
            .end(TIME_2.plusMinutes(30))
            .build()

    public static final Training TRAINING_3 = Training.builder()
            .id('training_3')
            .customerId(CUSTOMER)
            .teacherId(TEACHER)
            .poolId(POOL_2)
            .tubId(TUB_2)
            .start(TIME_2)
            .end(TIME_2.plusMinutes(30))
            .build()


    @Autowired
    ScheduleRepository repository

    @Autowired
    PoolScheduleController controller

    @Autowired
    ScheduleApplicationProperties properties

    @AfterEach
    void 'clear database'() {
        repository.deleteAll()
    }

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
                .teacherId(TEACHER)
                .poolId(POOL_1)
                .tubId(TUB_1)
                .start(time)
                .end(time.plusMinutes(properties.getDuration()))
                .build())
        repository.insert(Training.builder()
                .id('training_2')
                .customerId(CUSTOMER)
                .teacherId(TEACHER)
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
