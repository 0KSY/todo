package com.solo.todo.member.service;

import com.solo.todo.member.entity.Member;
import com.solo.todo.member.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class MemberService {

    private final MemberRepository memberRepository;

    public MemberService(MemberRepository memberRepository) {
        this.memberRepository = memberRepository;
    }

    public Member createMember(Member member){

        return memberRepository.save(member);
    }

    public Member updateMember(Member member){
        Member findMember = memberRepository.findById(member.getMemberId()).get();

        findMember.setNickname(member.getNickname());

        return memberRepository.save(findMember);

    }

    public Member findMember(long memberId){
        return memberRepository.findById(memberId).get();
    }

    public Page<Member> findMembers(int page, int size){
        return memberRepository.findAll(
                PageRequest.of(page, size, Sort.by("memberId").descending())
        );
    }

    public void deleteMember(long memberId){
        memberRepository.deleteById(memberId);
    }
}
