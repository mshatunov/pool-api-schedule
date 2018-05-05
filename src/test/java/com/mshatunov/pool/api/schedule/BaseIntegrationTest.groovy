package com.mshatunov.pool.api.schedule

import com.mshatunov.pool.api.schedule.clent.InstructorClient
import com.mshatunov.pool.api.schedule.clent.dto.Instructor
import com.mshatunov.pool.api.schedule.configuration.ScheduleApplicationProperties
import com.mshatunov.pool.api.schedule.repository.ScheduleRepository
import com.mshatunov.pool.api.schedule.repository.model.Training
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.context.annotation.Profile
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

import java.time.LocalDateTime

import static org.mockito.ArgumentMatchers.anyString
import static org.mockito.Mockito.when

@Profile("test")
@SpringBootTest
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = [ScheduleApplication])
class BaseIntegrationTest {

    public static final String CUSTOMER = 'customer_1234'
    public static final String POOL_1 = 'pool_1234'
    public static final String POOL_2 = 'pool_5678'
    public static final String TUB_1 = '1234_left'
    public static final String TUB_2 = '1234_right'

    public static final LocalDateTime TIME_1 = LocalDateTime.of(2018, 4, 1, 10, 30)
    public static final LocalDateTime TIME_2 = LocalDateTime.of(2020, 4, 1, 10, 30)

    public static final Training TRAINING_1 = Training.builder()
            .id('training_1')
            .customerId(CUSTOMER)
            .poolId(POOL_1)
            .tubId(TUB_1)
            .start(TIME_1)

            .end(TIME_1.plusMinutes(30))
            .build()

    public static final Training TRAINING_2 = Training.builder()
            .id('training_2')
            .customerId(CUSTOMER)
            .poolId(POOL_1)
            .tubId(TUB_2)
            .start(TIME_2)
            .end(TIME_2.plusMinutes(30))
            .build()

    public static final Training TRAINING_3 = Training.builder()
            .id('training_3')
            .customerId(CUSTOMER)
            .poolId(POOL_2)
            .tubId(TUB_2)
            .start(TIME_2)
            .end(TIME_2.plusMinutes(30))
            .build()

    public static final String INSTRUCTOR_ID = 'instructor_1'
    public static final String INSTRUCTOR_NAME = 'instructor_name_1'
    public static final String INSTRUCTOR_LAST_NAME = 'instructor_last_name_1'

    public static final Instructor INSTRUCTOR = Instructor.builder()
            .id(INSTRUCTOR_ID)
            .name(INSTRUCTOR_NAME)
            .lastName(INSTRUCTOR_LAST_NAME)
            .build()


    @Autowired
    ScheduleRepository repository
    @Autowired
    ScheduleApplicationProperties properties

    @MockBean
    InstructorClient instructorClient

    @BeforeEach
    void 'prepare tests'() {
        repository.deleteAll()
        when(instructorClient.getInstructorByPoolAndDate(anyString(), anyString(), anyString())) thenReturn(INSTRUCTOR)
    }

    @Test
    void contextLoads() {
    }

}
