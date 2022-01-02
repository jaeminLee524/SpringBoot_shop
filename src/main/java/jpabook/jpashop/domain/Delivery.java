package jpabook.jpashop.domain;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter @Setter
public class Delivery {

    @Id @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @OneToOne(mappedBy = "delivery")
    private Order order;

    @Embedded
    private Address address;

    // EnumType은 항상 String,
    // ORDINAL로 했을 경우 새로운 enum이 추가되면 data 순서가 밀려 컬럼값이 꼬이는 현상
    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;
}
