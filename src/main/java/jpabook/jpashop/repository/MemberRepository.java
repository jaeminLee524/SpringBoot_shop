package jpabook.jpashop.repository;

import jpabook.jpashop.domain.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class MemberRepository {

    private final EntityManager em;

    // 저장
    public Member save(Member member) {
        em.persist(member);

        return member;
    }

    // 한명의 멤버 조회
    // public <T> T find(Class<T> entityClass, ~) => entity 클래스로 반환
    public Member findOne(Long id) {
        return em.find(Member.class, id);
    }

    // 멤버 리스트 조회
    public List<Member> findAll() {
        List<Member> result = em.createQuery("select m from Member m", Member.class)
                .getResultList();

        return result;
    }

    // 이름으로 조회
    public List<Member> findByName(String name) {
        List<Member> result = em.createQuery("select m from Member m where name = :name", Member.class)
                .setParameter("name", name)
                .getResultList();

        return result;
    }
    
}