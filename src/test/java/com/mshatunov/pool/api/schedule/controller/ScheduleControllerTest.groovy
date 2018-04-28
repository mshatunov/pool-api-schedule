package com.mshatunov.pool.api.schedule.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.mshatunov.pool.api.schedule.BaseIntegrationTest
import com.mshatunov.pool.api.schedule.repository.ScheduleRepository
import com.mshatunov.pool.api.schedule.repository.model.Training
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired

import java.time.Duration
import java.time.LocalDateTime

import static org.junit.Assert.assertEquals

class ScheduleControllerTest extends BaseIntegrationTest {

    public static final String CUSTOMER = 'customer_1234'
    public static final String TEACHER = 'teacher_1234'
    public static final String POOL = 'pool_1234'

    public static final Training TRAINING_1 = Training.builder()
    .id('training_1')
    .customerId(CUSTOMER)
    .teacherId(TEACHER)
    .poolId(POOL)
    .start(LocalDateTime.of(2018,4,1,10,30))
    .duration(Duration.ofMinutes(30))
    .build()

    public static final Training TRAINING_2 = Training.builder()
            .id('training_2')
            .customerId(CUSTOMER)
            .teacherId(TEACHER)
            .poolId(POOL)
            .start(LocalDateTime.of(2018,4,8,10,30))
            .duration(Duration.ofMinutes(30))
            .build()

    @Autowired
    ScheduleRepository repository

    @Autowired
    ScheduleController controller

    @Autowired
    ObjectMapper mapper

    @AfterEach
    void 'clear database'() {
        repository.deleteAll()
    }

    @Test
    void 'successfully get empty customer trainings'() {
        def response = controller.getCustomerClasses(CUSTOMER)
        assertEquals(Collections.emptyList(), response)
    }

    @Test
    void 'successfully get all customer trainings'() {
        repository.insert(TRAINING_1)
        repository.insert(TRAINING_2)
        def response = controller.getCustomerClasses(CUSTOMER)
        assertEquals(2, response.size())
    }

}
