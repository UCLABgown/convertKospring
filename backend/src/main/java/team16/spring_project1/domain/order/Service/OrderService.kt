package team16.spring_project1.domain.order.Service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team16.spring_project1.domain.order.DTO.response.OrderResponseDTO
import team16.spring_project1.domain.order.Entity.Order
import team16.spring_project1.domain.order.Mapper.OrderMapper
import team16.spring_project1.domain.order.Repository.OrderRepository
import team16.spring_project1.global.enums.DeliveryStatus


@Service
class OrderService(private val orderRepository: OrderRepository, private val orderMapper: OrderMapper) {
    @Transactional
    fun createOrder(order: Order): Order {
        for (item in order.orderItems) {
            item.order = order
        }

        return orderRepository.save(order)
    }

    @Transactional
    fun createOrderDTO(order: Order): OrderResponseDTO {
        return orderMapper.toOrderResponseDTO(createOrder(order))
    }

    @get:Transactional(readOnly = true)
    val allOrders: List<Order>
        get() = orderRepository.findAll()

    @get:Transactional(readOnly = true)
    val allOrderDTO: List<OrderResponseDTO>
        get() = allOrders
            .stream()
            .map { order: Order? ->
                orderMapper.toOrderResponseDTO(
                    order!!
                )
            }
            .toList()

    @Transactional(readOnly = true)
    fun getOrdersByEmail(email: String): List<Order> {
        return orderRepository.findByEmail(email)
    }

    @Transactional(readOnly = true)
    fun getOrdersDTOByEmail(email: String): List<OrderResponseDTO> {
        return getOrdersByEmail(email)
            .stream()
            .map { order: Order? ->
                orderMapper.toOrderResponseDTO(
                    order!!
                )
            }
            .toList()
    }

    @Transactional(readOnly = true)
    fun getOrderById(id: Long): Order {
        return orderRepository.findById(id).orElse(DEFAULT_ORDER)
    }

    @Transactional(readOnly = true)
    fun getOrderDTOById(id: Long): OrderResponseDTO {
        return orderMapper.toOrderResponseDTO(getOrderById(id))
    }

    @Transactional
    fun updateOrderStatusAndGetOrders(id: Long, status: DeliveryStatus): OrderResponseDTO {
        val order = getOrderById(id)

        if (order == DEFAULT_ORDER) {
            return orderMapper.toOrderResponseDTO(order)
        } else {
            order.status = status
            val updatedOrder = orderRepository.save(order)
            return orderMapper.toOrderResponseDTO(updatedOrder)
        }
    }

    @Transactional
    fun deleteOrder(id: Long): Boolean {
        if (!orderRepository.existsById(id)) {
            return false
        }

        orderRepository.deleteById(id)
        return true
    }

    fun updateOrderStatus(currentStatus: String, newStatus: String) {
        orderRepository.updateStatusByCurrentStatus(currentStatus, newStatus)
    }

    fun resetStatus() {
        orderRepository.resetAllStatuses(DeliveryStatus.PAYMENT_COMPLETED.name)
    }


    fun count(): Long {
        return orderRepository.count()
    }

    companion object {
        private val DEFAULT_ORDER = Order()
    }
}
