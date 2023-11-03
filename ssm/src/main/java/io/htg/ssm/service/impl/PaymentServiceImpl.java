package io.htg.ssm.service.impl;

import io.htg.ssm.model.Payment;
import io.htg.ssm.model.PaymentEvent;
import io.htg.ssm.model.PaymentState;
import io.htg.ssm.repo.PaymentRepo;
import io.htg.ssm.service.PaymentService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class PaymentServiceImpl implements PaymentService {
    private final StateMachineFactory<PaymentState, PaymentEvent> factory;
    private final PaymentRepo paymentRepo;
    private final PaymentStateChangeInterceptor paymentStateChangeInterceptor;

    public final static String PAYMENT_ID_HEADER = "payment-id";

    @Transactional
    @Override
    public Payment newPayment() {
        return null;
    }

    @Transactional
    @Override
    public StateMachine<PaymentState, PaymentEvent> preAuth(Long paymentId) {
        var stateMachine = build(paymentId);
        sendEvent(paymentId, PaymentEvent.PRE_AUTH_APPROVED, stateMachine);
        return stateMachine;
    }

    @Transactional
    @Override
    public StateMachine<PaymentState, PaymentEvent> authorize(Long paymentId) {
        var stateMachine = build(paymentId);
        sendEvent(paymentId, PaymentEvent.AUTH_APPROVED, stateMachine);
        return stateMachine;
    }

    @Transactional
    @Override
    public StateMachine<PaymentState, PaymentEvent> decline(Long paymentId) {
        var stateMachine = build(paymentId);
        sendEvent(paymentId, PaymentEvent.PRE_AUTH_DECLINED, stateMachine);
        return stateMachine;
    }

    private StateMachine<PaymentState, PaymentEvent> build(Long paymentId){
        Payment paymentInDB = paymentRepo.findById(paymentId).orElseThrow(RuntimeException::new);
        StateMachine<PaymentState, PaymentEvent> sm = factory.getStateMachine(paymentId.toString());

        sm.stop();

        sm.getStateMachineAccessor()
                .doWithAllRegions(
                        sma -> {
                            sma.resetStateMachine(
                                    new DefaultStateMachineContext<>(paymentInDB.getState(), null, null, null)
                            );
                            sma.addStateMachineInterceptor(paymentStateChangeInterceptor);
                        }
                );
        sm.start();
        return sm;
    }

    private void sendEvent(Long paymentID, PaymentEvent event, StateMachine<PaymentState, PaymentEvent> sm){
        Message<PaymentEvent> msg = MessageBuilder.withPayload(event)
                .setHeader(PAYMENT_ID_HEADER, paymentID)
                .build();
        sm.sendEvent(msg);
    }
}
