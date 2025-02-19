package team16.spring_project1.domain.order.Controller;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import team16.spring_project1.domain.member.Entity.Member;
import team16.spring_project1.domain.member.Service.MemberService;
import team16.spring_project1.domain.order.Entity.Order;
import team16.spring_project1.domain.order.Entity.OrderItem;
import team16.spring_project1.domain.order.Service.OrderService;
import team16.spring_project1.global.enums.DeliveryStatus;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
@Transactional
public class OrderControllerTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    MemberService memberService;

    @Autowired
    private MockMvc mvc;

    @Test
    @DisplayName("Post) 주문 생성 성공")
    void t1_1() throws Exception {

        //테스트 주문의 데이터 설정
        String email = "test@gmail.com";
        String status = "COMPLETED";
        int totalPrice = 50000;
        String productName = "원두";
        int count = 5;
        int price = 10000;

        //테스트 주문 생성
        ResultActions resultActions = mvc
                .perform(
                        post("/order")
                                .content(String.format("""    
                                        {
                                          "email": "%s",
                                          "status": "%s",
                                          "totalPrice": %d,
                                          "orderItems": [
                                            {
                                              "productName": "%s",
                                              "count": %d,
                                              "price": %d
                                            }
                                          ]
                                        }
                                        """, email, status, totalPrice, productName, count, price).stripIndent()
                                )
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        //Response 검증
        resultActions
                .andExpect(handler().handlerType(OrderController.class))
                .andExpect(handler().methodName("createOrder"))
                .andExpect(status().isCreated())
                //주문 정보 일치 여부 검증
                .andExpect(jsonPath("$.success").value((true)))
                .andExpect(jsonPath("$.content.email").value("test@gmail.com"))
                .andExpect(jsonPath("$.content.status").value("COMPLETED"))
                .andExpect(jsonPath("$.content.totalPrice").value(50000))
                .andExpect(jsonPath("$.content.orderItems[0].productName").value("원두"))
                .andExpect(jsonPath("$.content.orderItems[0].count").value(5))
                .andExpect(jsonPath("$.content.orderItems[0].price").value(10000));
    }

    @Test
    @DisplayName("Post) 주문 생성 실패")
    void t1_2() throws Exception {

        //잘못된 가격 데이터 설정 (타입 불일치)
        String wrongPrice = "not-a-number";

        //잘못된 주문 생성
        ResultActions resultActions = mvc
                .perform(
                        post("/order")
                                .content(String.format("""    
                                        {
                                          "email": "test@gmail.com",
                                          "status": "COMPLETED",
                                          "totalPrice": %s,
                                          "orderItems": [
                                            {
                                              "productName": "원두",
                                              "count": 5,
                                              "price": 10000
                                            }
                                          ]
                                        }
                                        """, wrongPrice).stripIndent()
                                )
                                .contentType(
                                        new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8)
                                )
                )
                .andDo(print());

        //Response 검증
        resultActions
                .andExpect(handler().handlerType(OrderController.class))
                .andExpect(handler().methodName("createOrder"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Get) 모든 주문 조회 성공")
    void t2_1() throws Exception {

        //모든 주문 조회
        List<Order> orders = orderService.getAllOrders();

        //API를 통한 주문 조회
        ResultActions resultActions = mvc
                .perform(
                        get("/order")
                )
                .andDo(print());

        //Response 검증
        resultActions
                .andExpect(handler().handlerType(OrderController.class))
                .andExpect(handler().methodName("getAllOrder"))
                .andExpect(status().isOk());

        //주문 순회
        for (int i = 1; i <= orders.size(); i++) {
            Order order = orders.get(i - 1);
            List<OrderItem> orderItems = order.getOrderItems();

            //주문 정보 일치 여부 검증
            resultActions
                    .andExpect(jsonPath(String.format("$.content[%d].email", i - 1)).value(order.getEmail()))
                    .andExpect(jsonPath(String.format("$.content[%d].status", i - 1)).value(order.getStatus().toString()))
                    .andExpect(jsonPath(String.format("$.content[%d].totalPrice", i - 1)).value(order.getTotalPrice()));

            //주문 상품 순회
            for (int j = 1; j <= orderItems.size(); j++) {
                OrderItem orderItem = orderItems.get(j - 1);

                //주문 상품 정보 일치 여부 검증
                resultActions
                        .andExpect(jsonPath(String.format("$.content[%d].orderItems[%d].productName", i - 1, j - 1)).value(orderItem.getProductName()))
                        .andExpect(jsonPath(String.format("$.content[%d].orderItems[%d].count", i - 1, j - 1)).value(orderItem.getCount()))
                        .andExpect(jsonPath(String.format("$.content[%d].orderItems[%d].price", i - 1, j - 1)).value(orderItem.getPrice()));
            }
        }
    }

    @Test
    @DisplayName("Get) 모든 주문 조회 실패")
    void t2_2() throws Exception {

            //주문 초기화
        orderService.getAllOrders().stream()
                .map(Order::getId)
                .toList()
                .forEach(id -> orderService.deleteOrder(id));

        //주문이 없는 상태에서 주문 조회
        ResultActions resultActions = mvc
                .perform(
                        get("/order")
                )
                .andDo(print());

        //Response 검증
        resultActions
                .andExpect(handler().handlerType(OrderController.class))
                .andExpect(handler().methodName("getAllOrder"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("주문이 존재하지 않습니다."))
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    @DisplayName("Get) 이메일 기준 주문 조회 성공")
    void t3_1() throws Exception {

        //존재하는 모든 이메일 정보 수집 (중복 제거)
        HashSet<String> emails = orderService.getAllOrders().stream()
                .map(Order::getEmail)
                .collect(Collectors.toCollection(HashSet::new));

        //이메일 정보 순회
        for (String email : emails) {
            //이메일 기준 주문 조회
            ResultActions resultActions = mvc
                    .perform(
                            get("/order/by-email")
                                    .param("email", email)
                    )
                    .andDo(print());

            //Response 검증
            resultActions
                    .andExpect(handler().handlerType(OrderController.class))
                    .andExpect(handler().methodName("getOrdersByEmail"))
                    .andExpect(status().isOk())
                     //이메일 일치 여부만 검증
                     //자세한 검증은 t3에서 수행한 바가 있으므로 생략
                    .andExpect(jsonPath("$.content[*].email").value(email));
        }
    }

    @Test
    @DisplayName("Get) 이메일 기준 주문 조회 실패")
    void t3_2() throws Exception {

        //존재하지 않는 이메일 설정
        String dummyEmail = "not-my-email@gmail.com";

        //존재하지 않는 이메일로 주문 조회
        ResultActions resultActions = mvc
                .perform(
                        get("/order/by-email")
                                .param("email", dummyEmail)
                )
                .andDo(print());

        //Response 검증
        resultActions
                .andExpect(handler().handlerType(OrderController.class))
                .andExpect(handler().methodName("getOrdersByEmail"))
                .andExpect(status().isOk())
                //조회된 이메일이 없는지(Content가 Empty인지) 검증
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    @DisplayName("Get) ID 기준 주문 조회 성공")
    void t4_1() throws Exception {

        //모든 주문 ID 수집(조건상 ID는 중복일 수 없음)
        List<Long> orderIds = orderService.getAllOrders().stream()
                .map(Order::getId)
                .toList();

        //모든 주문 ID 순회
        for (Long orderId : orderIds) {
            //ID 기준 주문 조회
            ResultActions resultActions = mvc
                    .perform(
                            get("/order/" + orderId)
                    )
                    .andDo(print());

            //Response 검증
            resultActions
                    .andExpect(handler().handlerType(OrderController.class))
                    .andExpect(handler().methodName("getOrderById"))
                    .andExpect(status().isOk())
                    //조회한 ID와 주문의 ID가 일치하는지 검증
                    .andExpect(jsonPath("$.content.id").value(orderId));
        }
    }

    @Test
    @DisplayName("Get) ID 기준 주문 조회 실패")
    void t4_2() throws Exception {

        //2개의 실패 케이스 존재 : 잘못된 ID, 존재하지 않는 ID
        //1. 잘못된 범위의 ID 설정
        Long wrongId = -1L;

        //잘못된 ID로 주문 조회
        ResultActions resultActions1 = mvc
                .perform(
                        get("/order/" + wrongId)
                )
                .andDo(print());

        //Response 검증
        resultActions1
                .andExpect(handler().handlerType(OrderController.class))
                .andExpect(handler().methodName("getOrderById"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.message").value("유효하지 않은 ID 값입니다."));

        //2. 존재하지 않는 ID 설정
        Long notExistingId = (long)orderService.getAllOrders().size() + 1;

        //존재하지 않는 ID로 주문 조회
        ResultActions resultActions2 = mvc
                .perform(
                        get("/order/" + notExistingId)
                )
                .andDo(print());

        //Response 검증
        resultActions2
                .andExpect(handler().handlerType(OrderController.class))
                .andExpect(handler().methodName("getOrderById"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.message").value("주문이 존재하지 않습니다."));
    }

    @Test
    @DisplayName("Put) 주문 상태 변경 성공")
    void t5_1() throws Exception {
        Member member = memberService.findByUsername("admin").get();
        String memberAuthToken = memberService.getAuthToken(member);

        //무작위 주문을 추출
        //해당 주문의 상태를 다음 상태로 변경
        List<Order> orders = orderService.getAllOrders();
        Order randomOrder = orders.get(new Random().nextInt(orders.size()));
        DeliveryStatus oldStatus = randomOrder.getStatus();
        DeliveryStatus newStatus = DeliveryStatus.values()[(oldStatus.ordinal() + 1) % DeliveryStatus.values().length];

        //주문 상태 변경
        ResultActions resultActions = mvc
                .perform(
                        put("/order/" + randomOrder.getId() + "/status")
                        .header("Authorization", "Bearer " + memberAuthToken)
                        .param("status", newStatus.toString()
                        )
                ).andDo(print());

        //Response 검증
        resultActions
                .andExpect(handler().handlerType(OrderController.class))
                .andExpect(handler().methodName("updateOrderStatus"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content.status").value(newStatus.toString())); //상태가 변경되었는지 검증
    }

    @Test
    @DisplayName("Put) 주문 상태 변경 실패")
    void t5_2() throws Exception {
        Member member = memberService.findByUsername("admin").get();
        String memberAuthToken = memberService.getAuthToken(member);

        //존재하지 않는 ID에 대해서는 t4-1에서 테스트한 바가 있음
        //따라서 존재하지 않는 상태에 대해서만 테스트
        String wrongStatus = "NOT-EXISTING-STATUS";

        //랜덤한 주문의 상태를 잘못된 상태로 변경
        ResultActions resultActions = mvc
                .perform(
                        put("/order/" + new Random(orderService.getAllOrders().size()) + "/status")
                                .param("status", wrongStatus)
                                .header("Authorization", "Bearer " + memberAuthToken)
                ).andDo(print());

        //Response 검증
        resultActions
                .andExpect(handler().handlerType(OrderController.class))
                .andExpect(handler().methodName("updateOrderStatus"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Delete) 주문 삭제 성공")
    void t6_1() throws Exception {
        Member member = memberService.findByUsername("admin").get();
        String memberAuthToken = memberService.getAuthToken(member);

        //랜덤한 주문 추출
        List<Order> orders = orderService.getAllOrders();
        Order randomOrder = orders.get(new Random().nextInt(orders.size()));
        Long randomOrderId = randomOrder.getId();

        //랜덤한 주문 삭제
        ResultActions resultActionsBefore = mvc
                .perform(
                         delete("/order/" + randomOrderId)
                        .header("Authorization", "Bearer " + memberAuthToken)
                )
                .andDo(print());

        //Response 검증
        resultActionsBefore
                .andExpect(handler().handlerType(OrderController.class))
                .andExpect(handler().methodName("deleteOrder"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("주문이 성공적으로 삭제되었습니다."));

        //Id 검색을 통해 제대로 삭제되었는지 검증
        ResultActions resultActionsAfter = mvc
                .perform(
                        get("/order/" + randomOrderId)
                )
                .andDo(print());

        //Response 검증
        resultActionsAfter
                .andExpect(handler().handlerType(OrderController.class))
                .andExpect(handler().methodName("getOrderById"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("주문이 존재하지 않습니다."))
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    @DisplayName("Delete) 주문 삭제 실패")
    void t6_2() throws Exception {
        Member member = memberService.findByUsername("admin").get();
        String memberAuthToken = memberService.getAuthToken(member);

        //존재하지 않는 ID 설정
        Long notExistingId = (long)orderService.getAllOrders().size() + 1;

        //존재하지 않는 ID로 주문 삭제
        ResultActions resultActions = mvc
                .perform(
                         delete("/order/" + notExistingId)
                        .header("Authorization", "Bearer " + memberAuthToken)
                )
                .andDo(print());

        //Response 검증
        resultActions
                .andExpect(handler().handlerType(OrderController.class))
                .andExpect(handler().methodName("deleteOrder"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("해당 주문은 이미 삭제되었거나 존재하지 않습니다."))
                .andExpect(jsonPath("$.content").isEmpty());
    }
}
