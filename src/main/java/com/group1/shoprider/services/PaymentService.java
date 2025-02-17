package com.group1.shoprider.services;

import com.group1.shoprider.dtos.payment.PaymentRequestDto;
import com.group1.shoprider.models.Payment;
import com.group1.shoprider.enums.PaymentStatus;
import com.group1.shoprider.repository.RepositoryPayment;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@NoArgsConstructor
@AllArgsConstructor
public class PaymentService {

    private RepositoryPayment repositoryPayment;

    public PaymentService(RepositoryPayment repositoryPayment,
                          @Value("${stripe.api.key}") String apiKey) {
        this.repositoryPayment = repositoryPayment;
        // Configurer Stripe avec la clé API
        Stripe.apiKey = apiKey;
    }

    /**
     * Créer un paiement avec Stripe
     */
    public String createPayment(PaymentRequestDto paymentRequest) throws StripeException {
        // Préparer les données pour créer un PaymentIntent
        Map<String, Object> params = new HashMap<>();
        params.put("amount", paymentRequest.getAmount().multiply(new BigDecimal(100)).intValue()); // Montant en centimes
        params.put("currency", paymentRequest.getCurrency());
        params.put("payment_method_types", List.of(paymentRequest.getPaymentMethod()));

        // Créer un PaymentIntent via l'API Stripe
        PaymentIntent paymentIntent = PaymentIntent.create(params);

        // Sauvegarder le paiement dans la base de données
        Payment payment = new Payment();
        payment.setAmount(paymentRequest.getAmount());
        payment.setCurrency(paymentRequest.getCurrency());
        payment.setPaymentMethod(paymentRequest.getPaymentMethod());
        payment.setStatus(PaymentStatus.PENDING);
        payment.setUserId(paymentRequest.getUserId());
        repositoryPayment.save(payment);

        // Retourner l'identifiant du PaymentIntent pour le front-end (client) Stripe
        return paymentIntent.getClientSecret();
    }

    /**
     * Valider le statut du paiement (Stripe Webhooks ou Verification)
     */
    public PaymentStatus validatePayment(String paymentIntentId) throws StripeException {
        // Récupérer le PaymentIntent via l'API Stripe
        PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

        // Vérifier le statut du PaymentIntent
        if ("succeeded".equals(paymentIntent.getStatus())) {
            return PaymentStatus.COMPLETED;
        }
        return PaymentStatus.FAILED;
    }
}
