package team16.spring_project1.domain.order.Mapper

import org.springframework.stereotype.Component
import team16.spring_project1.domain.order.DTO.response.OrderItemResponseDTO
import team16.spring_project1.domain.order.DTO.response.OrderResponseDTO
import team16.spring_project1.domain.order.Entity.Order
import team16.spring_project1.domain.order.Entity.OrderItem

@Component
class OrderMapper {
    fun toOrderResponseDTO(order: Order): OrderResponseDTO {
        order.status
        order.orderItems
        return OrderResponseDTO(
            order.id,
            order.email,
            order.status.name,
            order.totalPrice,
            order.createDate,
            order.modifyDate,
            order.orderItems.stream().map { item: OrderItem -> this.toOrderItemResponseDTO(item) }.toList()
        )
    }

    fun toOrderItemResponseDTO(item: OrderItem): OrderItemResponseDTO {
        return OrderItemResponseDTO(
            item.id!!,
            item.productName,
            item.count,
            item.price,
            item.createDate!!,
            item.modifyDate!!
        )
    }
}
