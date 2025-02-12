package com.group1.shoprider.services;

import com.group1.shoprider.dtos.instrument.DTOInstrument;
import com.group1.shoprider.dtos.order.OrderRequest;
import com.group1.shoprider.dtos.order.OrderResult;
import com.group1.shoprider.dtos.orderitem.OrderItemRequest;
import com.group1.shoprider.exceptions.InstrumentNotFoundException;
import com.group1.shoprider.models.Instrument;
import com.group1.shoprider.models.Order;
import com.group1.shoprider.models.OrderItem;
import com.group1.shoprider.models.User;
import com.group1.shoprider.repository.RepositoryInstrument;
import com.group1.shoprider.repository.RepositoryOrder;
import com.group1.shoprider.repository.RepositoryUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiceOrder {

    private final RepositoryInstrument repositoryInstrument;
    private final RepositoryOrder repositoryOrder;
    private final RepositoryUser repositoryUser;

    @Transactional
    public OrderResult passerCommande(List<OrderItemRequest> orderRequest) {
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
}