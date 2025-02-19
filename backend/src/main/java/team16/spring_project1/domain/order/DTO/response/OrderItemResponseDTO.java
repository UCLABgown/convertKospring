package team16.spring_project1.domain.order.DTO.response;

import java.time.LocalDateTime;

public record OrderItemResponseDTO(
        Long id,
        String productName,
        int count,
        int price,
        LocalDateTime createdAt,
        LocalDateTime modifiedAt
) {

    public int getTotalPrice() {
        return count * price;
    }

}
