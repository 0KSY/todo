package com.solo.todo.auth.userDetailsService;

import com.solo.todo.auth.utils.CustomAuthorityUtils;
import com.solo.todo.exception.BusinessLogicException;
import com.solo.todo.exception.ExceptionCode;
import com.solo.todo.member.entity.Member;
import com.solo.todo.member.repository.MemberRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final CustomAuthorityUtils customAuthorityUtils;

    public CustomUserDetailsService(MemberRepository memberRepository, CustomAuthorityUtils customAuthorityUtils) {
        this.memberRepository = memberRepository;
        this.customAuthorityUtils = customAuthorityUtils;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Member> optionalMember = memberRepository.findByEmail(username);

        Member findMember = optionalMember.orElseThrow(
                () -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        CustomUserDetails customUserDetails = new CustomUserDetails();
        customUserDetails.setMemberId(findMember.getMemberId());
        customUserDetails.setEmail(findMember.getEmail());
        customUserDetails.setNickname(findMember.getNickname());
        customUserDetails.setPassword(findMember.getPassword());
        customUserDetails.setRoles(findMember.getRoles());

        customUserDetails.setAuthorities(customAuthorityUtils.createAuthorities(findMember.getRoles()));

        return customUserDetails;

    }


}
