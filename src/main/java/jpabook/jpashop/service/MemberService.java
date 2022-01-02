package jpabook.jpashop.service;


import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true) // 조회성 메서드에 적용
@RequiredArgsConstructor //final 변수들로 생성자 만듬
public class MemberService {

    private final MemberRepository memberRepository;

    /**
     * 회원가입
     */
    @Transactional
    public Long join(Member member) {
        // 중복 회원 체크
        validateDuplicateMember(member);

        // em.persist가 되는 순간, persistenceContext에 member객체를 올림,
        // key는 db와 매핑한 id값이 되고, 값은 @GeneratedValue로 값이 생심 => persist하는 당시에 항상 값이 세팅되어있음
        // member.getId는 항상 값이 있다는게 보장됨
        return member.getId();
    }

    private void validateDuplicateMember(Member member) {
        List<Member> findMembers = memberRepository.findByName(member.getName());
        if(!findMembers.isEmpty() ) {
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }
    }

    /**
     * 회원 전체 조회
     */
    public List<Member> findMembers() {
        return memberRepository.findAll();
    }

    /**
     * 회원 한명 조회
     */
}