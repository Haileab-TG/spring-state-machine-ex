package io.htg.ssm.service.impl;

import io.htg.ssm.model.PaymentEvent;
import io.htg.ssm.model.PaymentState;
import io.htg.ssm.repo.PaymentRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PaymentStateChangeInterceptor extends StateMachineInterceptorAdapter<PaymentState, PaymentEvent> {
    private final PaymentRepo paymentRepo;
    @Override
    public void preStateChange(State<PaymentState, PaymentEvent> state, Message<PaymentEvent> message, Transition<PaymentState, PaymentEvent> transition, StateMachine<PaymentState, PaymentEvent> stateMachine, StateMachine<PaymentState, PaymentEvent> rootStateMachine) {
        Optional.ofNullable(message)
                .ifPresent(msg -> Optional.ofNullable(Long.valueOf(
                        (msg.getHeaders().getOrDefault(PaymentServiceImpl.PAYMENT_ID_HEADER, 1L)).toString()
                )).ifPresent(paymentId -> {
                    paymentRepo.findById(paymentId).ifPresent(payment -> {
                        payment.setState(state.getId());
                        paymentRepo.save(payment);
                    });
                })
        );
    }


}
