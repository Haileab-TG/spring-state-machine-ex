package io.htg.ssm.config;

import io.htg.ssm.model.PaymentEvent;
import io.htg.ssm.model.PaymentState;
import io.htg.ssm.service.impl.PaymentServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListener;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;
import java.util.Random;

@Slf4j
@EnableStateMachineFactory
@Configuration
public class StateMachineConfig extends StateMachineConfigurerAdapter<PaymentState, PaymentEvent> {

    @Override
    public void configure(StateMachineStateConfigurer<PaymentState, PaymentEvent> states) throws Exception {
        states.withStates()
                .initial(PaymentState.NEW)
                .states(EnumSet.allOf(PaymentState.class))
                .end(PaymentState.AUTH)
                .end(PaymentState.AUTH_ERROR)
                .end(PaymentState.PRE_AUTH_ERROR);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<PaymentState, PaymentEvent> transitions) throws Exception {
        transitions.withExternal().source(PaymentState.NEW).target(PaymentState.NEW).event(PaymentEvent.PRE_AUTHORIZE)
                    .action(preAuthAction())
                .and()
                .withExternal().source(PaymentState.NEW).target(PaymentState.PRE_AUTH).event(PaymentEvent.PRE_AUTH_APPROVED)
                .and()
                .withExternal().source(PaymentState.PRE_AUTH).target(PaymentState.PRE_AUTH).event(PaymentEvent.AUTH)
                .action(authAction())
                .and()
                .withExternal().source(PaymentState.PRE_AUTH).target(PaymentState.AUTH).event(PaymentEvent.AUTH_APPROVED)

                .and()
                .withExternal().source(PaymentState.NEW).target(PaymentState.PRE_AUTH_ERROR).event(PaymentEvent.PRE_AUTH_DECLINED)
                .and()
                .withExternal().source(PaymentState.PRE_AUTH).target(PaymentState.AUTH_ERROR).event(PaymentEvent.AUTH_DECLINED);
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<PaymentState, PaymentEvent> config) throws Exception {
        StateMachineListener<PaymentState, PaymentEvent> listenerAdapter =  new StateMachineListenerAdapter<>(){
            @Override
            public void stateChanged(State<PaymentState, PaymentEvent> from, State<PaymentState, PaymentEvent> to) {
                System.out.println("State changed from " + from.getId() + " to " + to.getId());
            }
        };
        config.withConfiguration().listener(listenerAdapter);

    }

    public Action<PaymentState, PaymentEvent> preAuthAction(){
        return context -> {
            if(new Random().nextInt(10) < 8){
                context.getStateMachine().sendEvent(
                        MessageBuilder.withPayload(PaymentEvent.PRE_AUTH_APPROVED)
                                .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER,
                                        context.getMessageHeaders().get(PaymentServiceImpl.PAYMENT_ID_HEADER)
                                ).build()
                );
                System.out.println("PreAuth approved");
            }else {
                context.getStateMachine().sendEvent(
                        MessageBuilder.withPayload(PaymentEvent.PRE_AUTH_DECLINED)
                                .setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER,
                                        context.getMessageHeaders().get(PaymentServiceImpl.PAYMENT_ID_HEADER)
                                ).build()
                );
                System.out.println("PreAuth Declined");
            }
        };
    }

    public Action<PaymentState, PaymentEvent> authAction(){
        return context -> {
            if(new Random().nextInt(10) < 8){
                context.getStateMachine().sendEvent(
                        MessageBuilder
                                .withPayload(PaymentEvent.AUTH_APPROVED)
                                .setHeader(
                                        PaymentServiceImpl.PAYMENT_ID_HEADER,
                                        context.getMessageHeaders().get(PaymentServiceImpl.PAYMENT_ID_HEADER)
                                )
                                .build()
                );
                System.out.println("Auth Approved");
            }else {
                context.getStateMachine().sendEvent(
                        MessageBuilder
                                .withPayload(PaymentEvent.AUTH_DECLINED)
                                .setHeader(
                                        PaymentServiceImpl.PAYMENT_ID_HEADER,
                                        context.getMessageHeaders().get(PaymentServiceImpl.PAYMENT_ID_HEADER)
                                )
                                .build()
                );
                System.out.println("Auth Declined");
            }
        };
    }
}
