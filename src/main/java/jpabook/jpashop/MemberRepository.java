package jpabook.jpashop;

import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class MemberRepository {

    @PersistenceContext
    private EntityManager em;

    // id 반환이유 : command랑 query를 분리하라는 원칙에 의거하여 저장 후 
    // sideEffect를 발생하는 command 성으로 return을 거의 만들지 않지만 id만 반환해 재조회 가능하도록 설계
    public Long save(Member member) {
        em.persist(member);
        return member.getId();
    }

    public Member find(Long id) {
        return em.find(Member.class, id);
    }
}
