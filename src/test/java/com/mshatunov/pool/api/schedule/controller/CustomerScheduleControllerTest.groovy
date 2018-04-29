package com.mshatunov.pool.api.schedule.controller

import com.fasterxml.jackson.databind.ObjectMapper
import com.mshatunov.pool.api.schedule.BaseIntegrationTest
import com.mshatunov.pool.api.schedule.configuration.ScheduleApplicationProperties
import com.mshatunov.pool.api.schedule.controller.dto.NewTrainingRequest
import com.mshatunov.pool.api.schedule.exception.TimeAlreadyOccupiedException
import com.mshatunov.pool.api.schedule.exception.TrainingNotBelongToCustomerException
import com.mshatunov.pool.api.schedule.repository.ScheduleRepository
import com.mshatunov.pool.api.schedule.repository.model.Training
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.util.StringUtils

import java.time.LocalDateTime

import static org.junit.jupiter.api.Assertions.*

class CustomerScheduleControllerTest extends BaseIntegrationTest {

    public static final String CUSTOMER = 'customer_1234'
    public static final String TEACHER = 'teacher_1234'
    public static final String POOL = 'pool_1234'
    public static final String TUB = '1234_left'
    public static final LocalDateTime TIME_1 = LocalDateTime.of(2018, 4, 1, 10, 30)
    public static final LocalDateTime TIME_2 = LocalDateTime.of(2020, 4, 1, 10, 30)

    public static final Training TRAINING_1 = Training.builder()
            .id('training_1')
            .customerId(CUSTOMER)
            .teacherId(TEACHER)
            .poolId(POOL)
            .tubId(TUB)
            .start(TIME_1)
            .end(TIME_1.plusMinutes(30))
            .build()

    public static final Training TRAINING_2 = Training.builder()
            .id('training_2')
            .customerId(CUSTOMER)
            .teacherId(TEACHER)
            .poolId(POOL)
            .tubId(TUB)
            .start(TIME_1)
            .end(TIME_2.plusMinutes(30))
            .build()

    @Autowired
    ScheduleRepository repository

    @Autowired
    CustomerScheduleController controller

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
                .tubId(TUB)
                .start(TIME_1)
                .build()
        def newTraining = controller.addCustomerTraining(CUSTOMER, request)

        assertNotNull(newTraining)

        assertEquals(TEACHER, newTraining.getTeacherId())
        assertEquals(POOL, newTraining.getPoolId())
        assertEquals(TUB, newTraining.getTubId())
        assertEquals(TIME_1, newTraining.getStart())
        assertEquals(TIME_1.plusMinutes(properties.getDuration()), newTraining.getEnd())

        def mongoTraining = repository.findById(newTraining.getId())
        assertTrue(mongoTraining.isPresent())
        assertEquals(CUSTOMER, mongoTraining.get().getCustomerId())
    }

    @Test
    void 'should throw exception if time is already occupied before'() {
        repository.insert(TRAINING_1)
        NewTrainingRequest request = NewTrainingRequest.builder()
                .teacherId(TEACHER)
                .poolId(POOL)
                .tubId(TUB)
                .build()

        request.setStart(TIME_1.minusMinutes(10))
        def action = { -> controller.addCustomerTraining(CUSTOMER, request) }
        assertThrows(TimeAlreadyOccupiedException.class, action)

        request.setStart(TIME_1)
        assertThrows(TimeAlreadyOccupiedException.class, action)

        request.setStart(TIME_1.plusMinutes(10))
        assertThrows(TimeAlreadyOccupiedException.class, action)
    }

    @Test
    void 'should throw exception if time is already occupied same'() {
        repository.insert(TRAINING_1)
        NewTrainingRequest request = NewTrainingRequest.builder()
                .teacherId(TEACHER)
                .poolId(POOL)
                .tubId(TUB)
                .build()

        request.setStart(TIME_1)
        def action = { -> controller.addCustomerTraining(CUSTOMER, request) }
        assertThrows(TimeAlreadyOccupiedException.class, action)
    }

    @Test
    void 'should throw exception if time is already occupied after'() {
        repository.insert(TRAINING_1)
        NewTrainingRequest request = NewTrainingRequest.builder()
                .teacherId(TEACHER)
                .poolId(POOL)
                .tubId(TUB)
                .build()

        request.setStart(TIME_1.plusMinutes(10))
        def action = { -> controller.addCustomerTraining(CUSTOMER, request) }
        assertThrows(TimeAlreadyOccupiedException.class, action)
    }

    @Test
    void 'should not throw exception if time is already occupied in another tub before'() {
        repository.insert(TRAINING_1)
        NewTrainingRequest request = NewTrainingRequest.builder()
                .teacherId(TEACHER)
                .poolId(POOL)
                .tubId(TUB + "another")
                .build()

        request.setStart(TIME_1.minusMinutes(10))
        assertFalse(StringUtils.isEmpty(controller.addCustomerTraining(CUSTOMER, request).getId()))
    }

    @Test
    void 'should not throw exception if time is already occupied in another tub same'() {
        repository.insert(TRAINING_1)
        NewTrainingRequest request = NewTrainingRequest.builder()
                .teacherId(TEACHER)
                .poolId(POOL)
                .tubId(TUB + "another")
                .build()

        request.setStart(TIME_1)
        assertFalse(StringUtils.isEmpty(controller.addCustomerTraining(CUSTOMER, request).getId()))
    }

    @Test
    void 'should not throw exception if time is already occupied in another tub after'() {
        repository.insert(TRAINING_1)
        NewTrainingRequest request = NewTrainingRequest.builder()
                .teacherId(TEACHER)
                .poolId(POOL)
                .tubId(TUB + "another")
                .build()

        request.setStart(TIME_1.plusMinutes(10))
        assertFalse(StringUtils.isEmpty(controller.addCustomerTraining(CUSTOMER, request).getId()))
    }

    @Test
    void 'successfully delete customer training'() {
        repository.insert(TRAINING_1)
        controller.deleteCustomerTraining(CUSTOMER, 'training_1')
        assertTrue(!repository.findById('training_1').isPresent())
    }

    @Test
    void 'unable to delete another customer\'s training'() {
        repository.insert(TRAINING_1)
        def action = { -> controller.deleteCustomerTraining('another_customer', 'training_1') }
        assertThrows(TrainingNotBelongToCustomerException.class, action)
    }

}
