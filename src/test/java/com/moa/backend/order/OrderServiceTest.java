package com.moa.backend.order;

import static org.assertj.core.api.Assertions.assertThat;

import com.moa.backend.domain.order.entity.DeliveryStatus;
import com.moa.backend.domain.order.entity.Order;
import com.moa.backend.domain.order.entity.OrderStatus;
import com.moa.backend.domain.order.repository.OrderRepository;
import com.moa.backend.domain.order.service.OrderService;
import com.moa.backend.domain.project.entity.Project;
import com.moa.backend.domain.project.repository.ProjectRepository;
import com.moa.backend.domain.user.entity.User;
import com.moa.backend.domain.user.repository.UserRepository;
import com.moa.backend.domain.maker.entity.Maker;
import com.moa.backend.domain.maker.repository.MakerRepository;
import jakarta.transaction.Transactional;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@Transactional
@ActiveProfiles("test")
class OrderServiceTest {

    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProjectRepository projectRepository;
    @Autowired
    private MakerRepository makerRepository;

    @Test
    @DisplayName("주문 생성부터 배송/구매 확정까지 전체 흐름")
    void orderLifecycle() {
        // given: 결제 전 상태의 주문
        Order order = createOrder();

        // when/then: 결제 완료 → 배송 → 배송완료 → 구매확정 순서대로 상태가 정상 전환되는지 확인
        order.markPaid();
        orderRepository.save(order);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);

        order.startDelivery();
        orderRepository.save(order);
        assertThat(order.getDeliveryStatus()).isEqualTo(DeliveryStatus.SHIPPING);

        order.completeDelivery();
        orderRepository.save(order);
        assertThat(order.getDeliveryStatus()).isEqualTo(DeliveryStatus.DELIVERED);

        order.confirm();
        orderRepository.save(order);
        assertThat(order.getDeliveryStatus()).isEqualTo(DeliveryStatus.CONFIRMED);
        assertThat(order.getStatus()).isEqualTo(OrderStatus.PAID);
    }

    private Order createOrder() {
        User user = userRepository.save(User.createUser(
                "order-test-" + UUID.randomUUID() + "@moa.com",
                "encodedPw",
                "주문테스터"
        ));
        Maker maker = makerRepository.save(Maker.create(user, "주문테스트메이커"));
        Project project = projectRepository.save(Project.builder()
                .maker(maker)
                .title("주문 테스트 프로젝트")
                .goalAmount(1000L)
                .build());

        return orderRepository.save(Order.create(
                user,
                project,
                "ORD-" + UUID.randomUUID(),
                "주문테스트",
                50_000L,
                "수령인",
                "010-0000-0000",
                "서울시",
                null,
                "12345"
        ));
    }
}
