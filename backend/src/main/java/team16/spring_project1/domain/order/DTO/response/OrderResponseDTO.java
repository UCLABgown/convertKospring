package team16.spring_project1.domain.order.DTO.response;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResponseDTO(
    Long id,
    String email,
    String status,
    int totalPrice,
    LocalDateTime createdAt,
    LocalDateTime modifiedAt,
    List<OrderItemResponseDTO> orderItems
) {
    public boolean isEmpty() {
        return this.id == null;
    }
}
