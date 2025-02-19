package team16.spring_project1.domain.order.Service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team16.spring_project1.domain.order.DTO.response.OrderResponseDTO;
import team16.spring_project1.domain.order.Entity.Order;
import team16.spring_project1.domain.order.Entity.OrderItem;
import team16.spring_project1.domain.order.Mapper.OrderMapper;
import team16.spring_project1.domain.order.Repository.OrderRepository;
import team16.spring_project1.global.enums.DeliveryStatus;

import java.util.List;

@Service
public class OrderService {
    private static final Order DEFAULT_ORDER = new Order();

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;

    public OrderService(OrderRepository orderRepository, OrderMapper orderMapper) {
        this.orderRepository = orderRepository;
        this.orderMapper = orderMapper;
    }

    @Transactional
    public Order createOrder(Order order) {

        for (OrderItem item : order.getOrderItems()) {
            item.setOrder(order);
        }

        return orderRepository.save(order);
    }

    @Transactional
    public OrderResponseDTO createOrderDTO(Order order) {
        return orderMapper.toOrderResponseDTO(createOrder(order));
    }

    @Transactional(readOnly = true)
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getAllOrderDTO() {
        return getAllOrders()
                .stream()
                .map(orderMapper::toOrderResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<Order> getOrdersByEmail(String email) {
        return orderRepository.findByEmail(email);
    }

    @Transactional(readOnly = true)
    public List<OrderResponseDTO> getOrdersDTOByEmail(String email) {
        return getOrdersByEmail(email)
                .stream()
                .map(orderMapper::toOrderResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElse(DEFAULT_ORDER);
    }

    @Transactional(readOnly = true)
    public OrderResponseDTO getOrderDTOById(Long id) {
        return orderMapper.toOrderResponseDTO(getOrderById(id));
    }

    @Transactional
    public OrderResponseDTO updateOrderStatusAndGetOrders(Long id, DeliveryStatus status) {
        Order order = getOrderById(id);

        if (order == DEFAULT_ORDER) {
            return orderMapper.toOrderResponseDTO(order);
        } else {
            order.setStatus(status);
            Order updatedOrder = orderRepository.save(order);
            return orderMapper.toOrderResponseDTO(updatedOrder);
        }
    }

    @Transactional
    public boolean deleteOrder(Long id) {
        if (!orderRepository.existsById(id)) {
            return false;
        }

        orderRepository.deleteById(id);
        return true;
    }

    public void updateOrderStatus(String currentStatus, String newStatus) {
        orderRepository.updateStatusByCurrentStatus(currentStatus, newStatus);
    }

    public void resetStatus() {
        orderRepository.resetAllStatuses(DeliveryStatus.PAYMENT_COMPLETED.name());
    }


    public long count() {
        return orderRepository.count();
    }
}
