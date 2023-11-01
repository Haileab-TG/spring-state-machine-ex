package io.htg.ssm.service;

import io.htg.ssm.model.Payment;
import io.htg.ssm.model.PaymentEvent;
import io.htg.ssm.model.PaymentState;
import org.springframework.statemachine.listener.StateMachineListener;

public interface PaymentService {
    Payment newPayment();
    StateMachineListener<PaymentState, PaymentEvent> preAuth(Long paymentId);
    StateMachineListener<PaymentState, PaymentEvent> authorize(Long paymentId);
    StateMachineListener<PaymentState, PaymentEvent> decline(Long paymentId);
}
