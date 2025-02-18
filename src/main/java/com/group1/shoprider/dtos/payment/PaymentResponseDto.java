package com.group1.shoprider.dtos.payment;

import com.group1.shoprider.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentResponseDto {
    private Long transactionId;
    private BigDecimal amount;
    private String paymentMethod;
    private String currency;
    private PaymentStatus status;
    private LocalDateTime timestamp;
}
