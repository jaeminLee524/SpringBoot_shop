package jpabook.jpashop.service;

import jpabook.jpashop.domain.Member;
import jpabook.jpashop.repository.MemberRepository;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest //springBoot를 띄운상태에서 실행, 해당 annotation이 없으면 @Autowired와 같은 애너테이션이 동작 x
@Transactional
public class MemberServiceTest {

    @Autowired private MemberService memberService;
    @Autowired private MemberRepository memberRepository;

    @Test
    public void 회원가입() throws Exception {
        //given
        Member member = new Member();
        member.setName("jaemin2");

        //when
        Long memberId = memberService.join(member);

        //then
        Assertions.assertThat(memberId).isEqualTo(member.getId());
        Assertions.assertThat(member.getName()).isEqualTo("jaemin2");
    }

    @Test
    public void 중복_회원_예약() throws Exception {
        //given
        Member member1 = new Member();
        member1.setName("jaemin1");

        Member member2 = new Member();
        member2.setName("jaemin1");

        //when
        memberService.join(member1);
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> memberService.join(member2));
        //then
        Assertions.assertThat(e.getMessage()).isEqualTo("이미 존재하는 회원입니다.");
    }
}