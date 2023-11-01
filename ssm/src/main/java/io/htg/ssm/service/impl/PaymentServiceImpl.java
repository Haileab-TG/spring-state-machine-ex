package io.htg.ssm.service.impl;

import io.htg.ssm.model.Payment;
import io.htg.ssm.model.PaymentEvent;
import io.htg.ssm.model.PaymentState;
import io.htg.ssm.repo.PaymentRepo;
import io.htg.ssm.service.PaymentService;
import lombok.RequiredArgsConstructor;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {
    private final StateMachineFactory<PaymentState, PaymentEvent> factory;
    private final PaymentRepo paymentRepo;


    @Override
    public Payment newPayment() {
        return null;
    }

    @Override
    public StateMachineListener<PaymentState, PaymentEvent> preAuth(Long paymentId) {
        return null;
    }

    @Override
    public StateMachineListener<PaymentState, PaymentEvent> authorize(Long paymentId) {
        return null;
    }

    @Override
    public StateMachineListener<PaymentState, PaymentEvent> decline(Long paymentId) {
        return null;
    }
}
