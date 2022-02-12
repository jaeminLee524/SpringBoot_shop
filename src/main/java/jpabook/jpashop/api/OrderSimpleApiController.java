package jpabook.jpashop.api;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Order;
import jpabook.jpashop.domain.OrderStatus;
import jpabook.jpashop.repository.OrderRepository;
import jpabook.jpashop.repository.OrderSearch;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryDto;
import jpabook.jpashop.repository.order.simplequery.OrderSimpleQueryRepository;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * XToOne(ManyToOne, OneToOne)
 * Order
 * Order -> Member
 * Order -> Delivery
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class OrderSimpleApiController { // Lazy로딩과 조회 성능 최적화

    private final OrderRepository orderRepository;
    private final OrderSimpleQueryRepository orderSimpleQueryRepository;

    @GetMapping("/v1/simple-orders") //v1 : entity직접 노출
    public List<Order> ordersV1() {
        //1. entity를 노출하게 되면 양방향 관계가 걸린 곳에서 무한 루프가 걸림 => @JsonIgnore로 해결
        //2. xToOne관계에서 LAZY로 발라논 객체들을 json이 해결을 하지 못함 entity를 갖고오는게 아닌 proxy객체를 참조하기에
        //      => hibernate5Module의 ForceLazyLoading으로 해결
        //3. entity를 그대로 노출하게되면 성능상에도 문제가 발생 => 원치않는 데이터의 query를 수행하기때문에 
        //      => 원하는 애만 뽑는 방법이 있긴함 강제로 Lazy를 초기화하면 됨 => proxy를 건드리면 entity객체를 갖고올 수 있음
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        for (Order order : all) {
            // 위의 3번 내용으로 Lazy강제 초기화
            order.getMember().getName();
            order.getMember().getAddress();
        }
        return all;
    }

    //v2의 문제점: LAZY로딩으로 인한 query가 많이 발생
    @GetMapping("/v2/simple-orders")
    public List<SimpleOrderDto> ordersV2() { //v2: dto로
        List<Order> orders = orderRepository.findAll();
        List<SimpleOrderDto> result = orders.stream().map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    // entity로 조회 -> dto로 변환
    @GetMapping("/v3/simple-orders")
    public List<SimpleOrderDto> ordersV3() {
        //ex) 조회결과 데이터 : 2개
        // => map을 수행하면서 N + 1 문제 발생 => 1 + 회원 N + 배송 N
        List<Order> orders = orderRepository.findAllWithMemberDelivery();
        List<SimpleOrderDto> result = orders.stream()
                .map(o -> new SimpleOrderDto(o))
                .collect(Collectors.toList());

        return result;
    }

    // v3보다는 원하는 결과 fit하게 갖고올 수 있다는 장점으로 성능 최적화가 가능(생각보다 미비함)
    // 하지만 dto에 맞게 구현된 로직이라 재활용할 수가 없다 => api 스펙에 맞춘 코드
    // 조회하는 field의 개수가 많고 실시간 트래픽이 많다면 api에 스펙에 맞춰 해당 로직을 구현하는걸 고려하는게 맞음
    @GetMapping("v4/simple-orders")
    public List<OrderSimpleQueryDto> ordersV4() {
        return orderSimpleQueryRepository.findOrderDtos();
    }


    @Data
    static class SimpleOrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private OrderStatus orderStatus;
        private Address address;

        public SimpleOrderDto(Order order) {
            orderId = order.getId();
            orderDate = order.getOrderDate();
            orderStatus = order.getStatus();
            name = order.getMember().getName(); //LAZY 초기화
            address = order.getDelivery().getAddress(); //LAZY 초기화
        }
    }

}
