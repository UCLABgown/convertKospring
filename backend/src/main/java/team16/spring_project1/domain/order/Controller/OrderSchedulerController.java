package team16.spring_project1.domain.order.Controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team16.spring_project1.domain.order.Service.OrderService;
import team16.spring_project1.global.apiResponse.ApiResponse;
import team16.spring_project1.scheduler.OrderDeliveryScheduler;

@RestController
@RequestMapping("/order/scheduler")
@Tag(name = "Order Scheduler Controller", description = "주문 스케줄러 TEST API, 즉시 스케줄러 작동을 확인할 수 있게 해줍니다.")
public class OrderSchedulerController {

    private final OrderDeliveryScheduler orderSchedulerController;
    private final OrderService orderService;

    public OrderSchedulerController(OrderDeliveryScheduler orderSchedulerController, OrderService orderService) {
        this.orderSchedulerController = orderSchedulerController;
        this.orderService = orderService;
    }

    @Operation(summary = "EVERYTHING TO PAYMENT_COMPLETED", description = "모든 ORDER을 PAYMENT_COMPLETED 상태로 초기화 합니다.")
    @GetMapping("/payment_completed")
    public ResponseEntity<ApiResponse<String>> resetStatus() {
        orderService.resetStatus();
        return ResponseEntity.ok(ApiResponse.success("초기화 완료"));
    }

    @Operation(summary = "PAYMENT_COMPLETED TO PREPARING", description = "PAYMENT_COMPLETED 상태를 PREPARING 상태로 업데이트 합니다.")
    @GetMapping("/prepare")
    public ResponseEntity<ApiResponse<String>> runPrepareDelivery() {
        orderSchedulerController.prePareDelivery();
        return ResponseEntity.ok(ApiResponse.success("스케줄러 배송 준비 성공"));
    }

    @Operation(summary = "PREPARING TO SHIPPING", description = "PREPARING 상태를 SHIPPING 상태로 업데이트 합니다.")
    @GetMapping("/shipped")
    public ResponseEntity<ApiResponse<String>> runStartDelivery() {
        orderSchedulerController.startDelivery();
        return ResponseEntity.ok(ApiResponse.success("스케줄러 배송 성공"));
    }

    @Operation(summary = "SHIPPING TO COMPLETED", description = "SHIPPING 상태를 COMPLETED 상태로 업데이트 합니다.")
    @GetMapping("/completed")
    public ResponseEntity<ApiResponse<String>> runEndDelivery() {
        orderSchedulerController.endDelivery();
        return ResponseEntity.ok(ApiResponse.success("초기화 성공"));
    }
}
