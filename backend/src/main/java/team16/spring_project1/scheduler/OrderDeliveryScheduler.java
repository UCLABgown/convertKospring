package team16.spring_project1.scheduler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import team16.spring_project1.domain.order.Service.OrderService;
import team16.spring_project1.global.enums.DeliveryStatus;

@Component
@Slf4j
public class OrderDeliveryScheduler {

    private final OrderService orderService;

    @Autowired
    public OrderDeliveryScheduler(OrderService orderService) {
        this.orderService = orderService;
    }

    // 정기 스케줄러
    @Scheduled(cron = "${scheduler.cron.prepare}")
    public void prePareDelivery() {
        orderService.updateOrderStatus(DeliveryStatus.PAYMENT_COMPLETED.name(), DeliveryStatus.PREPARING.name());
    }

    @Scheduled(cron = "${scheduler.cron.shipped}")
    public void startDelivery() {
        orderService.updateOrderStatus(DeliveryStatus.PREPARING.name(), DeliveryStatus.SHIPPING.name());
    }

    @Scheduled(cron = "${scheduler.cron.completed}")
    public void endDelivery() {
        orderService.updateOrderStatus(DeliveryStatus.SHIPPING.name(), DeliveryStatus.COMPLETED.name());
    }

    // 테스트 스케줄러, 테스트 로직이 필요할 시 사용
/*

    @Scheduled(cron = "${scheduler.cron.test.prepare}")
    public void testPrePareDelivery() {
        orderService.updateOrderStatus(DeliveryStatus.PAYMENT_COMPLETED.name(), DeliveryStatus.PREPARING.name());
    }

    @Scheduled(cron = "${scheduler.cron.test.shipped}")
    public void testStartDelivery() {
        orderService.updateOrderStatus(DeliveryStatus.PREPARING.name(), DeliveryStatus.SHIPPING.name());
    }

    @Scheduled(cron = "${scheduler.cron.test.completed}")
    public void testEndDelivery() {
        orderService.updateOrderStatus(DeliveryStatus.SHIPPING.name(), DeliveryStatus.COMPLETED.name());
    }

    @Scheduled(cron = "${scheduler.cron.test.reset}")
    public void testInitDelivery() {
        orderService.resetStatus();

    }
*/

}
