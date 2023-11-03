package io.htg.ssm.service;

import io.htg.ssm.model.Payment;
import io.htg.ssm.model.PaymentEvent;
import io.htg.ssm.model.PaymentState;
import org.springframework.statemachine.StateMachine;

public interface PaymentService {
    Payment newPayment();
    StateMachine<PaymentState, PaymentEvent> preAuth(Long paymentId);
    StateMachine<PaymentState, PaymentEvent> authorize(Long paymentId);
    StateMachine<PaymentState, PaymentEvent> decline(Long paymentId);
}
