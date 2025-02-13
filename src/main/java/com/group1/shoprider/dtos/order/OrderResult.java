package com.group1.shoprider.dtos.order;

import com.group1.shoprider.dtos.instrument.DTOInstrument;
import com.group1.shoprider.models.Order;
import com.group1.shoprider.models.OrderItem;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class OrderResult {
    private Long orderId;
    private LocalDateTime dateOfPayment;
    private Double totalPrice;
    private List<DTOInstrument> instruments;

    // Constructeur pour créer l'objet OrderResult
    public OrderResult(Long orderId, LocalDateTime dateOfPayment, Double totalPrice, List<DTOInstrument> instruments) {
        this.orderId = orderId;
        this.dateOfPayment = dateOfPayment;
        this.totalPrice = totalPrice;
        this.instruments = instruments;
    }

    // Méthode principale pour convertir un objet Order en OrderResult
    public static OrderResult toOrderResult(Order order) {
        // Convertir les OrderItem en DTOInstrument via la méthode dédiée
        List<DTOInstrument> dtoInstruments = convertOrderItemsToDTO(order.getItems());

        // Retourner un nouvel objet OrderResult
        return new OrderResult(
                order.getId(),
                order.getDateOfPayment(),
                order.getTotalPrice(),
                dtoInstruments
        );
    }

    // Nouvelle méthode dédiée à la conversion des OrderItem en DTOInstrument
    private static List<DTOInstrument> convertOrderItemsToDTO(List<OrderItem> orderItems) {
        return DTOInstrument.convertToDTOInstrumentList(orderItems.stream()
                .map(OrderItem::getInstrument) // Obtenir les instruments de chaque OrderItem
                .toList());
    }
}
