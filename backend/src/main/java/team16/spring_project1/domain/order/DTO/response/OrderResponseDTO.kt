package team16.spring_project1.domain.order.DTO.response

import java.time.LocalDateTime

@JvmRecord
data class OrderResponseDTO(
    val id: Long?,
    val email: String?,
    val status: String?,
    val totalPrice: Int?,
    val createdAt: LocalDateTime?,
    val modifiedAt: LocalDateTime?,
    val orderItems: List<OrderItemResponseDTO>?
) {
    val isEmpty: Boolean
        get() = this.id == null
}
