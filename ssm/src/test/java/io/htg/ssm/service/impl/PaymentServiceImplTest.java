package io.htg.ssm.service.impl;

import io.htg.ssm.model.Payment;
import io.htg.ssm.model.PaymentState;
import io.htg.ssm.repo.PaymentRepo;
import io.htg.ssm.service.PaymentService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PaymentServiceImplTest {
    @Autowired
    PaymentRepo paymentRepo;
    @Autowired
    PaymentService paymentService;
    Payment payment;


    @BeforeEach
    void setUp() {
        payment = Payment.builder()
                .amount(new BigDecimal("12.99"))
                .build();
    }

    @Test
    void preAuth() {
        payment.setState(PaymentState.NEW);
        payment = paymentRepo.save(payment);
        System.out.println("State before preAuth " + payment.getState());
        System.out.println(payment.getId());
        paymentService.preAuth(payment.getId());
        payment = paymentRepo.findById(payment.getId()).get();
        System.out.println("State after preAuth " + payment.getState());
    }

    @Test
    void authorize(){
        payment.setState(PaymentState.PRE_AUTH);
        payment = paymentRepo.save(payment);
        paymentService.authorize(payment.getId());
        payment = paymentRepo.findById(payment.getId()).get();
        System.out.println("State after AUTH " + payment.getState());
    }

}