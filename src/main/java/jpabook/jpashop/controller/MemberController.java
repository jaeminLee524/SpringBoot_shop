package jpabook.jpashop.controller;

import jpabook.jpashop.domain.Address;
import jpabook.jpashop.domain.Member;
import jpabook.jpashop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @GetMapping("/members/new")
    public String createForm(Model model) {

        model.addAttribute("memberForm", new MemberForm());

        return "members/createMemberForm";
    }

    @PostMapping("/members/new")
    // @Valid를 통해 MemberForm에 있는 @NotEmpty와 같은 validation을 체크함
    // MemberForm으로 화면에 맞는 객체를 만드는게 맞음
    public String create(@Valid MemberForm memberForm, BindingResult result) {

        if( result.hasErrors() ) {
            //createMemberForm에서 thymeleaf로 에러를 처리 함
            return "members/createMemberForm";
        }

        //memberForm로 넘어온 데이터로 멤버 초기화
        Address address = new Address(memberForm.getCity(), memberForm.getStreet(), memberForm.getZipcode());

        Member member = new Member();
        member.setName(member.getName());
        member.setAddress(address);

        memberService.join(member);

        return "redirect:/";
    }
}
