package com.sparta.delivery.payment.repository;

import com.sparta.delivery.payment.domain.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

}
