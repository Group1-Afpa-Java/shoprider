package com.group1.shoprider.controllers;

import com.group1.shoprider.dtos.payment.PaymentRequestDto;
import com.group1.shoprider.enums.PaymentStatus;
import com.group1.shoprider.models.Payment;
import com.group1.shoprider.repository.RepositoryPayment;
import com.group1.shoprider.services.PaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final RepositoryPayment repositoryPayment;
    private final PaymentService paymentService;

    /**
     * Démarrer un paiement
     */
    @PostMapping("/create")
    public ResponseEntity<String> createPayment(@Validated @RequestBody PaymentRequestDto paymentRequest) {
        try {
            // Appeler le service Stripe pour créer un paiement
            String clientSecret = paymentService.createPayment(paymentRequest);
            return ResponseEntity.ok(clientSecret); // Retourner le client secret de PaymentIntent
        } catch (StripeException e) {
            return ResponseEntity.badRequest().body("Erreur lors de la création du paiement : " + e.getMessage());
        }
    }
    @PostMapping("/webhook")
    public ResponseEntity<String> stripeWebhook(@RequestBody String payload,
                                                @RequestHeader("Stripe-Signature") String signature) {
        try {
            // Vérifiez la signature pour sécuriser l'appel Stripe
            Event event = Webhook.constructEvent(payload, signature, "YOUR_WEBHOOK_SECRET");

            // Traitez l'événement (par exemple, "payment_intent.succeeded")
            if ("payment_intent.succeeded".equals(event.getType())) {
                PaymentIntent paymentIntent = (PaymentIntent) event.getDataObjectDeserializer()
                        .getObject()
                        .orElseThrow(() -> new IllegalStateException("Erreur de désérialisation"));
                System.out.println("Paiement réussi pour : " + paymentIntent.getId());

                // Mettre à jour le statut de votre paiement dans la base de données
                // Exemple : rechercher le paiement et le passer à COMPLETED
                Payment payment = repositoryPayment.findByPaymentIntentId(paymentIntent.getId())
                        .orElseThrow(() -> new IllegalStateException("Paiement introuvable"));
                payment.setStatus(PaymentStatus.COMPLETED);
                repositoryPayment.save(payment);
            }
            return ResponseEntity.ok("Webhook reçu avec succès");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors du traitement du webhook : " + e.getMessage());
        }
    }

    /**
     * Vérifier le statut du paiement
     */
    @GetMapping("/status/{paymentIntentId}")
    public ResponseEntity<PaymentStatus> getPaymentStatus(@PathVariable String paymentIntentId) {
        try {
            PaymentStatus status = paymentService.validatePayment(paymentIntentId);
            return ResponseEntity.ok(status);
        } catch (StripeException e) {
            return ResponseEntity.badRequest().body(PaymentStatus.FAILED);
        }
    }
}