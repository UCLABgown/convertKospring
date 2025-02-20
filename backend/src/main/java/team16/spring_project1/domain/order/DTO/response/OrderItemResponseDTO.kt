package team16.spring_project1.domain.order.DTO.response

import java.time.LocalDateTime

@JvmRecord
data class OrderItemResponseDTO(
    val id: Long,
    val productName: String,
    val count: Int,
    val price: Int,
    val createdAt: LocalDateTime,
    val modifiedAt: LocalDateTime
) {
    val totalPrice: Int
        get() = count * price
}
