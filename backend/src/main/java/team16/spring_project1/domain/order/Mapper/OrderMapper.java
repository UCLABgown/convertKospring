package team16.spring_project1.domain.order.Mapper;

import org.springframework.stereotype.Component;
import team16.spring_project1.domain.order.DTO.response.OrderItemResponseDTO;
import team16.spring_project1.domain.order.DTO.response.OrderResponseDTO;
import team16.spring_project1.domain.order.Entity.Order;
import team16.spring_project1.domain.order.Entity.OrderItem;

import java.util.List;

@Component
public class OrderMapper {

    public OrderResponseDTO toOrderResponseDTO(Order order) {
        return new OrderResponseDTO(
                order.getId(),
                order.getEmail(),
                order.getStatus() != null ? order.getStatus().name() : "UNKNOWN",
                order.getTotalPrice(),
                order.getCreateDate(),
                order.getModifyDate(),
                order.getOrderItems() != null
                        ? order.getOrderItems().stream().map(this::toOrderItemResponseDTO).toList()
                        : List.of()
        );
    }

    public OrderItemResponseDTO toOrderItemResponseDTO(OrderItem item) {
        return new OrderItemResponseDTO(
                item.getId(),
                item.getProductName(),
                item.getCount(),
                item.getPrice(),
                item.getCreateDate(),
                item.getModifyDate()
        );
    }

}
