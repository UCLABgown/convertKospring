package team16.spring_project1.domain.order.Controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import lombok.RequiredArgsConstructor
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import team16.spring_project1.domain.order.DTO.response.OrderResponseDTO
import team16.spring_project1.domain.order.Entity.Order
import team16.spring_project1.domain.order.Service.OrderService
import team16.spring_project1.global.apiResponse.ApiResponse
import team16.spring_project1.global.enums.DeliveryStatus
import java.util.*


@RestController
@RequestMapping("/order")
@RequiredArgsConstructor
@Tag(name = "Order Controller", description = "주문 관리 REST API")
class OrderController (
    val orderService: OrderService
){


    @Operation(summary = "Create Order", description = "새로운 주문을 생성합니다.")
    @PostMapping
    fun createOrder(@Valid@RequestBody order:  Order): ResponseEntity<ApiResponse<OrderResponseDTO>> {
        val savedOrder = orderService.createOrderDTO(order)
        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ApiResponse.success(savedOrder))
    }

    @get:GetMapping
    @get:Operation(summary = "Get All Orders", description = "모든 주문 목록을 가져옵니다.")
    val allOrder: ResponseEntity<ApiResponse<List<OrderResponseDTO>>>
        get() {
            val orders = orderService.allOrderDTO

            return if (orders.isEmpty()) {
                ResponseEntity.ok(
                    ApiResponse.success(
                        "주문이 존재하지 않습니다.",
                        orders
                    )
                )
            } else {
                ResponseEntity.ok(
                    ApiResponse.success(
                        orders
                    )
                )
            }
        }

    @Operation(summary = "Get Orders by Email", description = "이메일을 기준으로 주문 목록을 가져옵니다.")
    @GetMapping("/by-email")
    fun getOrdersByEmail(@RequestParam email: String?): ResponseEntity<ApiResponse<List<OrderResponseDTO>>> {
        if (email.isNullOrEmpty()) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure("유효하지 않은 email 값입니다."))
        }

        val orders = orderService.getOrdersDTOByEmail(email)

        return if (orders.isEmpty()) {
            ResponseEntity.ok(
                ApiResponse.success(
                    "주문이 존재하지 않습니다."
                )
            )
        } else {
            ResponseEntity.ok(
                ApiResponse.success(
                    orders
                )
            )
        }
    }

    @Operation(summary = "Get Order by ID", description = "주문 ID를 기준으로 특정 주문을 가져옵니다.")
    @GetMapping("/{id}")
    fun getOrderById(@PathVariable id: Long): ResponseEntity<ApiResponse<OrderResponseDTO>> {

        if (id <= 0) {
            return ResponseEntity
                .badRequest()
                .body(ApiResponse.failure("유효하지 않은 ID 값입니다."))
        }

        val order = orderService.getOrderDTOById(id)
        return if (order.isEmpty) {
            ResponseEntity.ok(
                ApiResponse.success(
                    "주문이 존재하지 않습니다."
                )
            )
        } else {
            ResponseEntity.ok(
                ApiResponse.success(
                    order
                )
            )
        }
    }

    @Operation(summary = "Update Order Status", description = "주문 상태를 업데이트합니다.")
    @PutMapping("/{id}/status")
    fun updateOrderStatus(
        @PathVariable id: Long,
        @Parameter(
            description = "주문 상태 (가능한 값: PAYMENT_COMPLETED, PREPARING, SHIPPING, COMPLETED)",
            example = "PAYMENT_COMPLETED"
        ) @RequestParam status: String
    ): ResponseEntity<ApiResponse<OrderResponseDTO>> {
        val deliveryStatus = DeliveryStatus.valueOf(status.uppercase(Locale.getDefault()))

        val order = orderService.updateOrderStatusAndGetOrders(id, deliveryStatus)

        return if (order.isEmpty) {
            ResponseEntity.ok(
                ApiResponse.success(
                    "주문이 존재하지 않습니다."
                )
            )
        } else {
            ResponseEntity.ok(
                ApiResponse.success(
                    order
                )
            )
        }
    }

    @Operation(summary = "Delete Order", description = "주문을 삭제합니다.")
    @DeleteMapping("/{id}")
    fun deleteOrder(@PathVariable id: Long): ResponseEntity<ApiResponse<String>> {
        val exist = orderService.deleteOrder(id)

        return if (exist) {
            ResponseEntity.ok(
                ApiResponse.success(
                    "주문이 성공적으로 삭제되었습니다."
                )
            )
        } else {
            ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(
                    ApiResponse.failure(
                        "해당 주문은 이미 삭제되었거나 존재하지 않습니다."
                    )
                )
        }
    }
}
