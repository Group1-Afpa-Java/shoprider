package com.group1.shoprider.dtos.payment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentResponseDto {
    private Long transactionId;
    private BigDecimal amount;
    private String paymentMethod;
    private String currency;
    private String status;
    private LocalDateTime timestamp;
}
