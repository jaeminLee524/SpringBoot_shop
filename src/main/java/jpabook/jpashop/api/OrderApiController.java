package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderItem;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.query.OrderFlatDto;
import jpabook.jpashop.repository.order.query.OrderItemQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryDto;
import jpabook.jpashop.repository.order.query.OrderQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

import static java.util.stream.Collectors.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderApiController { //Collection조회

    private final OrderRepository orderRepository;
    private final OrderQueryRepository orderQueryRepository;

    //Entity 직접 노출
    @GetMapping("/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();

            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.forEach(o -> o.getItem().getName());
        }

        return all;
    }

    // Entity -> Dto
    @GetMapping("/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAll();
        List<OrderDto> result = orders.stream().map(o -> new OrderDto(o)).collect(toList());

        return result;
    }

    // collection조희의 fetch join을 이용
    @GetMapping("/v3/orders")
    public List<OrderDto> ordersV3() {
        List<Order> all = orderRepository.findAllWithItem();

        List<OrderDto> result = all.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());
        return result;
    }

    // 0. 1:N관계의 fetch join은 paging처리가 불가를 해결
    // 1. xToOne의 관계는 모두 fetch join => row수를 증가시키지 않음 => paging query에 영향X
    // 2. Collection은 LAZY Loading으로 조회
    // 3. LAZY Loading 최적화를 위해 hibernate.default.batch_fetch_size, @BatchSize를 적용 => IN Query가 적용됨
    @GetMapping("/v3.1/orders")
    public List<OrderDto> ordersV3_page(@RequestParam(value = "offset", defaultValue = "0") int offset,
                                        @RequestParam(value = "limit", defaultValue = "100") int limit) {
        List<Order> orders = orderRepository.findAllWithMemberDelivery(offset, limit);

        List<OrderDto> result = orders.stream()
                .map(o -> new OrderDto(o))
                .collect(toList());

        return result;
    }

    @GetMapping("/v4/orders")
    public List<OrderQueryDto> ordersV4() {
        return orderQueryRepository.findOrderQueryDtos();
    }

    @GetMapping("/v5/orders")
    public List<OrderQueryDto> ordersV5() {
        return orderQueryRepository.findAllByDto_optimization();
    }
    @GetMapping("/v6/orders")
    public List<OrderQueryDto> ordersV6() {
        List<OrderFlatDto> flats = orderQueryRepository.findAllByDto_flat();

        return flats.stream()
                .collect(groupingBy(o -> new OrderQueryDto(o.getOrderId(), o.getName(), o.getOrderDate(), o.getOrderStatus(), o.getAddress()),
                        mapping(o -> new OrderItemQueryDto(o.getOrderId(), o.getItemName(), o.getOrderPrice(), o.getCount()), toList())
                )).entrySet().stream()
                .map(e -> new OrderQueryDto(e.getKey().getOrderId(), e.getKey().getName(), e.getKey().getOrderDate(), e.getKey().getOrderStatus(), e.getKey().getAddress(), e.getValue()))
                .collect(toList());
    }

    @Data
    static class OrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;
        // TODO Entity -> dto
        // orderVersion2에서 return을 해줄땐 결국 OrderItem Entity spec을 그대로 노출하는 것이다
        // entity를 외부에 노출하지 말라는 것은 단순히 dto로 감싸서 보내라는 것이 아닌, entity의 의존을 모두 끊어야 한다
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            address = order.getDelivery().getAddress();
            //orderItems = order.getOrderItems();
            orderItems = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(toList());
        }
    }

    //위에서 언급한 OrderItem의 Entity를 dto로 변환
    @Data
    static class OrderItemDto {

        //client 입장에서 name, price, count만 있으면 되는 시나리오
        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }

}
