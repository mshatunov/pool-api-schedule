package com.mshatunov.pool.api.schedule.exception;

public class TrainingNotBelongToCustomerException extends ScheduleException {
    public TrainingNotBelongToCustomerException(String customerId, String trainingId) {
        super(String.format("Training %s doesn't belong to customer %s", trainingId, customerId));
    }
}
