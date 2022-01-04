package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Order;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OrderRepository {

    private final EntityManager em;

    /**
     * 주문 저장
     */
    public void save(Order order) {
        em.persist(order);
    }

    /**
     *  단건 주문 조회
     */
    public Order findOne(Long id) {
        return em.find(Order.class, id);
    }

    /**
     * 조회
     */
    public List<Order> findAll(OrderSearch orderSearch){
        List<Order> resultList = em.createQuery("select o from Order o join o.member m" +
                        "where o.status= :status" +
                        "and m.name like :name", Order.class)
                .getResultList();
        return resultList;
    }

}
