package team16.spring_project1.domain.order.Controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team16.spring_project1.domain.order.DTO.response.OrderResponseDTO;
import team16.spring_project1.domain.order.Entity.Order;
import team16.spring_project1.domain.order.Service.OrderService;
import team16.spring_project1.global.apiResponse.ApiResponse;
import team16.spring_project1.global.enums.DeliveryStatus;

import java.util.List;

@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@Tag(name = "Order Controller", description = "주문 관리 REST API")
public class OrderController {

    private final OrderService orderService;

    @Operation(summary = "Create Order", description = "새로운 주문을 생성합니다.")
    @PostMapping
    public ResponseEntity<ApiResponse<OrderResponseDTO>> createOrder(@Valid @RequestBody Order order) {

        OrderResponseDTO savedOrder = orderService.createOrderDTO(order);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(savedOrder));

    }

    @Operation(summary = "Get All Orders", description = "모든 주문 목록을 가져옵니다.")
    @GetMapping
    public ResponseEntity<ApiResponse<List<OrderResponseDTO>>> getAllOrder() {
        List<OrderResponseDTO> orders = orderService.getAllOrderDTO();

        if (orders.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.success("주문이 존재하지 않습니다.", orders));
        } else {
            return ResponseEntity.ok(ApiResponse.success(orders));
        }
    }

    @Operation(summary = "Get Orders by Email", description = "이메일을 기준으로 주문 목록을 가져옵니다.")
    @GetMapping("/by-email")
    public ResponseEntity<ApiResponse<List<OrderResponseDTO>>> getOrdersByEmail(@RequestParam String email) {
        if (email == null || email.isEmpty()) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.failure("유효하지 않은 email 값입니다."));
        }

        List<OrderResponseDTO> orders = orderService.getOrdersDTOByEmail(email);

        if (orders.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.success("주문이 존재하지 않습니다."));
        } else {
            return ResponseEntity.ok(ApiResponse.success(orders));
        }
    }

    @Operation(summary = "Get Order by ID", description = "주문 ID를 기준으로 특정 주문을 가져옵니다.")
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> getOrderById(@PathVariable Long id) {
        if (id <= 0) {
            return ResponseEntity
                    .badRequest()
                    .body(ApiResponse.failure("유효하지 않은 ID 값입니다."));
        }

        OrderResponseDTO order = orderService.getOrderDTOById(id);

        if (order.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.success("주문이 존재하지 않습니다."));
        } else {
            return ResponseEntity.ok(ApiResponse.success(order));
        }
    }

    @Operation(summary = "Update Order Status", description = "주문 상태를 업데이트합니다.")
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponse<OrderResponseDTO>> updateOrderStatus(
            @PathVariable Long id,
            @Parameter(
                    description = "주문 상태 (가능한 값: PAYMENT_COMPLETED, PREPARING, SHIPPING, COMPLETED)",
                    example = "PAYMENT_COMPLETED"
            )
            @RequestParam String status) {

        DeliveryStatus deliveryStatus = DeliveryStatus.valueOf(status.toUpperCase());

        OrderResponseDTO order = orderService.updateOrderStatusAndGetOrders(id, deliveryStatus);

        if(order.isEmpty()) {
            return ResponseEntity.ok(ApiResponse.success("주문이 존재하지 않습니다."));
        } else {
            return ResponseEntity.ok(ApiResponse.success(order));
        }
    }

    @Operation(summary = "Delete Order", description = "주문을 삭제합니다.")
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteOrder(@PathVariable Long id) {
        boolean exist = orderService.deleteOrder(id);

        if (exist) {
            return ResponseEntity.ok(ApiResponse.success("주문이 성공적으로 삭제되었습니다."));
        } else {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.failure("해당 주문은 이미 삭제되었거나 존재하지 않습니다."));
        }
    }
}
