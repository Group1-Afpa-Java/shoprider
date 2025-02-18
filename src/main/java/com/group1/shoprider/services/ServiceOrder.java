package com.group1.shoprider.services;

import com.group1.shoprider.dtos.instrument.DTOInstrument;
import com.group1.shoprider.dtos.order.OrderRequest;
import com.group1.shoprider.dtos.order.OrderResult;
import com.group1.shoprider.dtos.orderitem.OrderItemRequest;
import com.group1.shoprider.dtos.payment.PaymentRequestDto;
import com.group1.shoprider.exceptions.InstrumentNotFoundException;
import com.group1.shoprider.services.PaymentService;
import com.group1.shoprider.models.Instrument;
import com.group1.shoprider.models.Order;
import com.group1.shoprider.models.OrderItem;
import com.group1.shoprider.models.User;
import com.group1.shoprider.repository.RepositoryInstrument;
import com.group1.shoprider.repository.RepositoryOrder;
import com.group1.shoprider.repository.RepositoryUser;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ServiceOrder {

    @Autowired
    private final PaymentService paymentService;
    @Autowired
    private final RepositoryInstrument repositoryInstrument;
    @Autowired
    private final RepositoryOrder repositoryOrder;
    @Autowired
    private final RepositoryUser repositoryUser;


    public ServiceOrder(
            PaymentService paymentService,
            RepositoryInstrument repositoryInstrument,
            RepositoryOrder repositoryOrder,
            RepositoryUser repositoryUser,
            @Value("${stripe.api.key}") String apiKey
    ) {
        this.paymentService = paymentService;
        this.repositoryInstrument = repositoryInstrument;
        this.repositoryOrder = repositoryOrder;
        this.repositoryUser = repositoryUser;
        Stripe.apiKey = apiKey;
    }

    @Transactional
    public OrderResult passerCommande(List<OrderItemRequest> orderRequest) throws StripeException{
        // Récupérer l'utilisateur connecté
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = repositoryUser.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Utilisateur introuvable"));

        // Créer une commande
        Order order = new Order();
        order.setUser(user);
        order.setDateOfPayment(LocalDateTime.now());
        order.setItems(new ArrayList<>());
        Double totalPrice = 0.0;

        // Ajouter chaque instrument à la commande
        for (var item : orderRequest) {
            Instrument instrument = repositoryInstrument.findById(item.getInstrumentId())
                    .orElseThrow(() -> new InstrumentNotFoundException("Instrument non disponible"));

            if (instrument.getQuantity() < item.getQuantity()) {
                throw new IllegalStateException("Quantité insuffisante pour " + instrument.getName());
            }

            // Décrémenter la quantité en stock
            instrument.setQuantity(instrument.getQuantity() - item.getQuantity());
            repositoryInstrument.save(instrument);

            // Créer un élément de commande
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setInstrument(instrument);
            orderItem.setQuantity(item.getQuantity());
            order.getItems().add(orderItem);

            totalPrice += instrument.getPrice() * item.getQuantity();
        }

        // Générer automatiquement le paiement pour l'ordre

        PaymentRequestDto paymentRequest = new PaymentRequestDto();
        paymentRequest.setAmount(new BigDecimal(totalPrice)); // Total de la commande
        paymentRequest.setCurrency("eur"); // Exemple : Devrait être dynamique si multi-devises
        paymentRequest.setPaymentMethod("card"); // Type de paiement à configurer selon les supports définis
        paymentRequest.setUserId(user.getId().toString()); // Identifier l'utilisateur dans le paiement

        // Appeler le service pour démarrer le paiement
        String clientSecret = paymentService.createPayment(paymentRequest);

        // Persister le lien entre la commande et le paiement (optionnel selon besoin)
        // Ajouter un champ `orderId` dans Payment pour lier une commande à un paiement.

        System.out.println("Paiement créé avec Stripe, Client Secret : " + clientSecret);


        order.setTotalPrice(totalPrice);
        repositoryOrder.save(order); // Sauvegarder la commande

        // Générer le "ticket de caisse" directement
        return generateOrderResult(order);
    }
    private OrderResult generateOrderResult(Order order) {
        // Extraire les détails des articles (instruments) dans la commande
        List<DTOInstrument> instruments = order.getItems().stream()
                .map(orderItem -> {
                    Instrument instrument = orderItem.getInstrument();
                    return new DTOInstrument(
                            instrument.getName(),
                            orderItem.getQuantity(),
                            instrument.getPrice(),
                            instrument.getType().getName()
                    );
                })
                .toList();

        // Construire et retourner l'objet OrderResult ("Ticket de caisse")
        OrderResult orderResult = new OrderResult();
        orderResult.setOrderId(order.getId());
        orderResult.setDateOfPayment(order.getDateOfPayment());
        orderResult.setTotalPrice(order.getTotalPrice());
        orderResult.setInstruments(instruments);
        return orderResult;
    }

    @Transactional(readOnly = true)
    public List<OrderResult> getOrderHistoryForCurrentUser() {
        // Récupérer le nom d'utilisateur de l'utilisateur connecté
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Vérifier si l'utilisateur existe et le récupérer
        User user = repositoryUser.findByUsername(username)
                .orElseThrow(() -> new IllegalStateException("Utilisateur introuvable"));

        // Récupérer les commandes de cet utilisateur
        List<Order> userOrders = repositoryOrder.findByUserId(user.getId());

        // Convertir les commandes en objets OrderResult
        return userOrders.stream()
                .map(this::generateOrderResult) // Utiliser la méthode existante pour la conversion
                .toList();
    }

    // Méthode pour récupérer toutes les commandes
    public List<Order> getAllOrders() {
        return repositoryOrder.findAll();
    }

    @Transactional(readOnly = true)
    public List<Map.Entry<String, Long>> getTopVentesParCategorie() {
        List<Order> allOrders = repositoryOrder.findAll();

        // Calculer les ventes par catégorie
        Map<String, Long> ventesParCategorie = allOrders.stream()
                .flatMap(order -> order.getItems().stream()) // Tous les articles de chaque commande
                .collect(Collectors.groupingBy(
                        orderItem -> orderItem.getInstrument().getType().getName(), // Regrouper par catégorie
                        Collectors.summingLong(OrderItem::getQuantity) // Compter les quantités vendues
                ));

        // Trier les catégories par nombre de ventes (descendant)
        return ventesParCategorie.entrySet().stream()
                .sorted((entry1, entry2) -> Long.compare(entry2.getValue(), entry1.getValue()))
                .toList();
    }
}