package com.group1.shoprider.services;

import com.group1.shoprider.dtos.payment.PaymentRequestDto;
import com.group1.shoprider.models.Payment;
import com.group1.shoprider.enums.PaymentStatus;
import com.group1.shoprider.repository.RepositoryPayment;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.net.RequestOptions;

import com.stripe.model.PaymentMethod;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class PaymentService {

    @Autowired
    private RepositoryPayment repositoryPayment;

    public PaymentService(RepositoryPayment repositoryPayment, @Value("${stripe.api.key}") String apiKey) {
        this.repositoryPayment = repositoryPayment;
        // Configurer Stripe avec la clé API
        Stripe.apiKey = apiKey;
    }

    /**
     * Créer un paiement avec Stripe
     */
    public String createPayment(PaymentRequestDto paymentRequest) throws StripeException {

        // Set Stripe API key
        RequestOptions requestOptions = RequestOptions.builder()
                .setApiKey(Stripe.apiKey)
                .build();

        // 1. Create a PaymentMethod
        Map<String, Object> automaticPaymentMethods = new HashMap<>();
        automaticPaymentMethods.put("enabled", true);
        automaticPaymentMethods.put("allow_redirects", "never"); // Prevent redirection
        Map<String, Object> paymentMethodParams = new HashMap<>();
        Map<String, Object> cardParams = new HashMap<>();
        cardParams.put("token", "tok_visa"); // Use test token
        paymentMethodParams.put("type", paymentRequest.getPaymentMethod()); // e.g., "card"
        paymentMethodParams.put("card", cardParams);

        PaymentMethod paymentMethod = PaymentMethod.create(paymentMethodParams, requestOptions);


        // 2. Prepare data for PaymentIntent
        Map<String, Object> params = new HashMap<>();
        params.put("amou" +
                "nt", paymentRequest.getAmount().multiply(new BigDecimal(100)).intValue()); // Amount in cents
        params.put("currency", paymentRequest.getCurrency());
        params.put("payment_method", paymentMethod.getId()); // Attach PaymentMethod ID
        params.put("confirm", true); // Automatically confirm the payment
        params.put("automatic_payment_methods", automaticPaymentMethods);

        // 3. Create PaymentIntent via Stripe API
        PaymentIntent paymentIntent = PaymentIntent.create(params, requestOptions);

        // 4. Save payment to the database (optional)
        Payment payment = new Payment();
        payment.setPaymentIntentId(paymentIntent.getId());
        System.out.println(paymentIntent.getId());
        payment.setAmount(paymentRequest.getAmount());
        payment.setCurrency(paymentRequest.getCurrency());
        payment.setPaymentMethod(paymentRequest.getPaymentMethod());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setUserId(paymentRequest.getUserId());
        repositoryPayment.save(payment);

        // 5. Return the client_secret for the frontend
        return paymentIntent.getClientSecret();
    }

    /**
     * Valider le statut du paiement (Stripe Webhooks ou Verification)
     */
    public PaymentStatus validatePayment(String paymentIntentId) throws StripeException {
        // Récupérer le PaymentIntent via l'API Stripe
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
        System.out.println(paymentIntent.getStatus());

        // Vérifier le statut du PaymentIntent
        if ("succeeded".equals(paymentIntent.getStatus())) {
            return PaymentStatus.COMPLETED;
        }
        return PaymentStatus.FAILED;
    }
}
