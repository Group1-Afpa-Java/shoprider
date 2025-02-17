package com.group1.shoprider.controllers;

import com.group1.shoprider.dtos.order.OrderRequest;
import com.group1.shoprider.dtos.order.OrderResult;
import com.group1.shoprider.dtos.orderitem.OrderItemRequest;
import com.group1.shoprider.models.Order;
import com.group1.shoprider.services.ServiceOrder;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/commandes")
public class OrderController {

    private final ServiceOrder serviceOrder;

    @Secured("CLIENT")
    @PostMapping("/passer")
    public ResponseEntity<OrderResult> passerCommande(@Valid @RequestBody List<OrderItemRequest> orderRequest) {
        System.out.println("hihi");
        try {
            // Appeler la méthode passerCommande et obtenir le ticket de caisse
            OrderResult orderResult = serviceOrder.passerCommande(orderRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(orderResult);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }

    }
    @GetMapping("/history")
    public ResponseEntity<List<OrderResult>> getOrderHistory() {
        List<OrderResult> orderHistory = serviceOrder.getOrderHistoryForCurrentUser();
        return ResponseEntity.ok(orderHistory);
    }

    @Secured({"ADMIN", "SUPER_ADMIN"})
    @GetMapping("/history_admin")
    public ResponseEntity<List<Order>> getHistoriqueCommandes() {
        List<Order> orders = serviceOrder.getAllOrders();
        return ResponseEntity.ok(orders);
    }

    @Secured({"ADMIN", "SUPER_ADMIN"})
    @GetMapping("/top-ventes/categorie")
    public ResponseEntity<List<Map.Entry<String, Long>>> getTopVentesParCategorie() {
        List<Map.Entry<String, Long>> topVentes = serviceOrder.getTopVentesParCategorie();
        return ResponseEntity.ok(topVentes);
    }
}