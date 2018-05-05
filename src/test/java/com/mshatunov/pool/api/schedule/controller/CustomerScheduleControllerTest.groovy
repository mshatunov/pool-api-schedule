package com.mshatunov.pool.api.schedule.controller

import com.mshatunov.pool.api.schedule.BaseIntegrationTest
import com.mshatunov.pool.api.schedule.controller.dto.NewTrainingRequest
import com.mshatunov.pool.api.schedule.exception.TimeAlreadyOccupiedException
import com.mshatunov.pool.api.schedule.exception.TrainingNotBelongToCustomerException
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.util.StringUtils

import static org.junit.jupiter.api.Assertions.*

class CustomerScheduleControllerTest extends BaseIntegrationTest {

    @Autowired
    CustomerScheduleController controller

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
                .poolId(POOL_1)
                .tubId(TUB_1)
                .start(TIME_1)
                .build()
        def newTraining = controller.addCustomerTraining(CUSTOMER, request)

        assertNotNull(newTraining)

        assertEquals(POOL_1, newTraining.getPoolId())
        assertEquals(TUB_1, newTraining.getTubId())
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
                .poolId(POOL_1)
                .tubId(TUB_1)
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
                .poolId(POOL_1)
                .tubId(TUB_1)
                .build()

        request.setStart(TIME_1)
        def action = { -> controller.addCustomerTraining(CUSTOMER, request) }
        assertThrows(TimeAlreadyOccupiedException.class, action)
    }

    @Test
    void 'should throw exception if time is already occupied after'() {
        repository.insert(TRAINING_1)
        NewTrainingRequest request = NewTrainingRequest.builder()
                .poolId(POOL_1)
                .tubId(TUB_1)
                .build()

        request.setStart(TIME_1.plusMinutes(10))
        def action = { -> controller.addCustomerTraining(CUSTOMER, request) }
        assertThrows(TimeAlreadyOccupiedException.class, action)
    }

    @Test
    void 'should not throw exception if time is already occupied in another tub before'() {
        repository.insert(TRAINING_1)
        NewTrainingRequest request = NewTrainingRequest.builder()
                .poolId(POOL_1)
                .tubId(TUB_1 + "another")
                .build()

        request.setStart(TIME_1.minusMinutes(10))
        assertFalse(StringUtils.isEmpty(controller.addCustomerTraining(CUSTOMER, request).getId()))
    }

    @Test
    void 'should not throw exception if time is already occupied in another tub same'() {
        repository.insert(TRAINING_1)
        NewTrainingRequest request = NewTrainingRequest.builder()
                .poolId(POOL_1)
                .tubId(TUB_1 + "another")
                .build()

        request.setStart(TIME_1)
        assertFalse(StringUtils.isEmpty(controller.addCustomerTraining(CUSTOMER, request).getId()))
    }

    @Test
    void 'should not throw exception if time is already occupied in another tub after'() {
        repository.insert(TRAINING_1)
        NewTrainingRequest request = NewTrainingRequest.builder()
                .poolId(POOL_1)
                .tubId(TUB_1 + "another")
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
