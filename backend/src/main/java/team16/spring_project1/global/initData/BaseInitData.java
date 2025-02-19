package team16.spring_project1.global.initData;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.transaction.annotation.Transactional;
import team16.spring_project1.domain.member.Entity.Member;
import team16.spring_project1.domain.member.Service.MemberService;
import team16.spring_project1.domain.order.Entity.Order;
import team16.spring_project1.domain.order.Entity.OrderItem;
import team16.spring_project1.domain.order.Service.OrderService;
import team16.spring_project1.domain.product.product.Service.ProductService;
import team16.spring_project1.domain.product.product.entity.Product;
import team16.spring_project1.global.enums.DeliveryStatus;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class BaseInitData {

    private final ProductService productService;
    private final OrderService orderService;
    private final MemberService memberService;

    private static final int ORDER_COUNT = 3;
    private static final int ITEM_PRICE = 500;
    private static final int ITEM_COUNT = 2;

    @Autowired
    @Lazy
    private BaseInitData self;

    @Bean
    public ApplicationRunner baseInitDataApplicationRunner() {
        return args -> {
            self.work1();
            self.work2();
            self.work3();
        };
    }

    @Transactional
    public void work1() {
        if (productService.count() > 0) return;
        Product product1 = productService.create(
                "원두커피 베트남 로부스타 G1 1kg 커피창고 고소한 맛있는 홀빈 콩",
                "커피콩",
                14900,
                "https://shop-phinf.pstatic.net/20240531_84/17171115333012Dsah_JPEG/118247422005482143_630751200.jpg"
        );
        Product product2 = productService.create(
                "테라로사 올데이 블렌드 원두커피 1.13kg",
                "커피콩",
                36900,
                "https://shopping-phinf.pstatic.net/main_1750344/17503448138.20190215162842.jpg"
        );
        Product product3 = productService.create(
                "스타벅스 커클랜드 원두커피 하우스 블렌드 1.13kg",
                "커피콩",
                23860,
                "https://shopping-phinf.pstatic.net/main_2461161/24611612527.1.20201027171504.jpg"
        );
        Product product4 = productService.create(
                "산미높은 약배전원두 에티오피아 코케허니 예가체프G1 스페셜티 갓볶은 원두홀빈 200g [원산지:에티오피아]",
                "커피콩",
                11200,
                "https://shop-phinf.pstatic.net/20241019_251/1729335546752dKz5H_JPEG/63468447768544782_588487658.jpg"
        );

        Product product5 = productService.create(
                "원두커피 베트남 로부스타 G1 1kg 커피창고 고소한 맛있는 홀빈 콩",
                "커피콩",
                14900,
                "https://shop-phinf.pstatic.net/20240531_84/17171115333012Dsah_JPEG/118247422005482143_630751200.jpg"
        );
        Product product6 = productService.create(
                "테라로사 올데이 블렌드 원두커피 1.13kg",
                "커피콩",
                36900,
                "https://shopping-phinf.pstatic.net/main_1750344/17503448138.20190215162842.jpg"
        );
        Product product7 = productService.create(
                "델몬트 미니 오렌지주스 레니니 미니병 250ml x 6병+스티커+크레이트 박스 코스트코",
                "쥬스",
                16790,
                "https://shop-phinf.pstatic.net/20240905_278/1725547677124GObqy_JPEG/8555278738581166_93595970.jpg"
        );
        Product product8 = productService.create(
                "돈시몬 오렌지주스 1L x 6병 착즙 100% 코스트코",
                "쥬스",
                19590,
                "https://shop-phinf.pstatic.net/20241119_253/17319847041521qBfc_PNG/16941289197835218_1831856195.png"
        );

        Product product9 = productService.create(
                "스타벅스 커클랜드 원두커피 하우스 블렌드 1.13kg",
                "커피콩",
                23860,
                "https://shopping-phinf.pstatic.net/main_2461161/24611612527.1.20201027171504.jpg"
        );
        Product product10 = productService.create(
                "산미높은 약배전원두 에티오피아 코케허니 예가체프G1 스페셜티 갓볶은 원두홀빈 200g [원산지:에티오피아]",
                "커피콩",
                11200,
                "https://shop-phinf.pstatic.net/20241019_251/1729335546752dKz5H_JPEG/63468447768544782_588487658.jpg"
        );
    }

    @Transactional
    public void work2() {
        if (orderService.count() > 0) return;

        IntStream.rangeClosed(1, ORDER_COUNT)
                .mapToObj(this::generateDummyOrder)
                .forEach(orderService::createOrder);
    }

    /**
     * 테스트용 Order 객체를 생성하는 메서드입니다.
     *
     * @param orderIndex Order 객체의 식별자 역할을 하는 숫자로, 이메일 주소와 OrderItem의 이름에 사용됩니다.
     * @return 가공된 Order 객체
     */
    private Order generateDummyOrder(int orderIndex) {
        Order order = new Order();
        order.setEmail("user" + orderIndex + "@example.com");
        order.setStatus(DeliveryStatus.PAYMENT_COMPLETED);

        List<OrderItem> orderItems = generateDummyOrderItems(orderIndex, order);

        // 총 가격 계산
        int totalPrice = orderItems.stream()
                .mapToInt(OrderItem::calculateTotalPrice)
                .sum();

        order.setTotalPrice(totalPrice);
        order.setOrderItems(orderItems);
        return order;
    }

    /**
     * 테스트용 OrderItems 객체를 생성하는 메서드입니다.
     * @param orderIndex orderItem의 이름 구별에 사용됩니다.
     * @param order orderItem의 ID 에 사용됩니다.
     * @return 가공된 orderItem 객체
     */
    @SuppressWarnings("all")
    private static List<OrderItem> generateDummyOrderItems(int orderIndex, Order order) {
        List<OrderItem> orderItems = IntStream.rangeClosed(1, 2)
                .mapToObj(itemIndex -> {
                    OrderItem item = new OrderItem();
                    item.setProductName("Item " + orderIndex + "-" + itemIndex);
                    item.setPrice(ITEM_PRICE);
                    item.setCount(ITEM_COUNT);
                    item.setOrder(order);
                    return item;
                })
                .collect(Collectors.toList());

        return orderItems;
    }

    @Transactional
    public void work3() {
        if (memberService.count() > 0) return;

        Member admin = memberService.join(
                "admin",
                "1234"
        );

        Member user1 = memberService.join(
                "user1",
                "1234"
        );
    }

}
