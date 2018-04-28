package com.mshatunov.pool.api.schedule.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.mshatunov.pool.api.schedule.BaseIntegrationTest
import com.mshatunov.pool.api.schedule.configuration.ScheduleApplicationProperties
import com.mshatunov.pool.api.schedule.controller.dto.NewTrainingRequest
import com.mshatunov.pool.api.schedule.repository.ScheduleRepository
import com.mshatunov.pool.api.schedule.repository.model.Training
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

import java.time.Duration
import java.time.LocalDateTime

import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull
import static org.junit.Assert.assertTrue

class ScheduleControllerTest extends BaseIntegrationTest {

    public static final String CUSTOMER = 'customer_1234'
    public static final String TEACHER = 'teacher_1234'
    public static final String POOL = 'pool_1234'

    public static final Training TRAINING_1 = Training.builder()
            .id('training_1')
            .customerId(CUSTOMER)
            .teacherId(TEACHER)
            .poolId(POOL)
            .start(LocalDateTime.of(2018, 4, 1, 10, 30))
            .duration(Duration.ofMinutes(30))
            .build()

    public static final Training TRAINING_2 = Training.builder()
            .id('training_2')
            .customerId(CUSTOMER)
            .teacherId(TEACHER)
            .poolId(POOL)
            .start(LocalDateTime.of(2020, 4, 8, 10, 30))
            .duration(Duration.ofMinutes(30))
            .build()

    @Autowired
    ScheduleRepository repository

    @Autowired
    ScheduleController controller

    @Autowired
    ObjectMapper mapper

    @Autowired
    ScheduleApplicationProperties properties

    @AfterEach
    void 'clear database'() {
        repository.deleteAll()
    }

    @Test
    void 'successfully get empty customer trainings'() {
        def response = controller.getCustomerTrainings(CUSTOMER, false)
        assertEquals(Collections.emptyList(), response)
    }

    @Test
    void 'successfully get all customer trainings'() {
        repository.insert(TRAINING_1)
        repository.insert(TRAINING_2)
        def response = controller.getCustomerTrainings(CUSTOMER, false)
        assertEquals(2, response.size())
    }

    @Test
    void 'successfully get only future customer trainings'() {
        repository.insert(TRAINING_1)
        repository.insert(TRAINING_2)
        def response = controller.getCustomerTrainings(CUSTOMER, true)
        assertEquals(1, response.size())
        assertEquals('training_2', response.get(0).getId())
    }

    @Test
    void 'successfully add new training for customer'() {
        NewTrainingRequest request = NewTrainingRequest.builder()
                .teacherId(TEACHER)
                .poolId(POOL)
                .start(LocalDateTime.of(2018, 4, 1, 10, 30))
                .build()
        def newTraining = controller.addCustomerTraining(CUSTOMER, request)

        assertNotNull(newTraining)

        assertEquals(TEACHER, newTraining.getTeacherId())
        assertEquals(POOL, newTraining.getPoolId())
        assertEquals(LocalDateTime.of(2018, 4, 1, 10, 30), newTraining.getStart())
        assertEquals(Duration.ofMinutes(properties.getDuration()), newTraining.getDuration())

        def mongoTraining = repository.findById(newTraining.getId())
        assertTrue(mongoTraining.isPresent())
        assertEquals(CUSTOMER, mongoTraining.get().getCustomerId())
    }

}
