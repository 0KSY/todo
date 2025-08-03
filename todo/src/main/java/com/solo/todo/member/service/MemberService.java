package com.solo.todo.member.service;

import com.solo.todo.auth.jwt.JwtTokenizer;
import com.solo.todo.auth.userDetailsService.CustomUserDetails;
import com.solo.todo.auth.utils.CustomAuthorityUtils;
import com.solo.todo.exception.BusinessLogicException;
import com.solo.todo.exception.ExceptionCode;
import com.solo.todo.member.entity.Member;
import com.solo.todo.member.repository.MemberRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@Transactional
public class MemberService {

    private final MemberRepository memberRepository;
    private final CustomAuthorityUtils customAuthorityUtils;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenizer jwtTokenizer;

    public MemberService(MemberRepository memberRepository, CustomAuthorityUtils customAuthorityUtils,
                         PasswordEncoder passwordEncoder, JwtTokenizer jwtTokenizer) {
        this.memberRepository = memberRepository;
        this.customAuthorityUtils = customAuthorityUtils;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenizer = jwtTokenizer;
    }

    public Member findVerifiedMember(long memberId){
        Optional<Member> optionalMember = memberRepository.findById(memberId);

        Member findMember = optionalMember
                .orElseThrow(() -> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        return findMember;
    }

    public void verifyExistsEmail(String email){
        Optional<Member> optionalMember = memberRepository.findByEmail(email);

        if(optionalMember.isPresent()){
            if(optionalMember.get().getSignupType() == (Member.SignupType.SERVER)){
                throw new BusinessLogicException(ExceptionCode.MEMBER_SERVER_USER);
            }
            else{
                throw new BusinessLogicException(ExceptionCode.MEMBER_GOOGLE_OAUTH2_USER);
            }
        }

    }

    public void verifyExistsNickname(String nickname){
        Optional<Member> optionalMember = memberRepository.findByNickname(nickname);

        if(optionalMember.isPresent()){
            throw new BusinessLogicException(ExceptionCode.MEMBER_NICKNAME_EXISTS);
        }
    }

    public void checkMemberId(long memberId, CustomUserDetails customUserDetails){

        if(memberId != customUserDetails.getMemberId()){
            throw new BusinessLogicException(ExceptionCode.MEMBER_NOT_MATCHED);
        }

    }


    public Member createMember(Member member){

        verifyExistsEmail(member.getEmail());
        verifyExistsNickname(member.getNickname());

        member.setSignupType(Member.SignupType.SERVER);

        String encryptedPassword = passwordEncoder.encode(member.getPassword());
        member.setPassword(encryptedPassword);

        List<String> roles = customAuthorityUtils.createRoles(member.getEmail());
        member.setRoles(roles);

        return memberRepository.save(member);
    }

    public Member updateMember(Member member, CustomUserDetails customUserDetails){
        Member findMember = findVerifiedMember(member.getMemberId());

        checkMemberId(findMember.getMemberId(), customUserDetails);

        Optional.ofNullable(member.getNickname())
                        .ifPresent(nickname -> {
                            if(!nickname.equals(findMember.getNickname())){
                                verifyExistsNickname(nickname);
                            }
                            findMember.setNickname(nickname);
                        });
        Optional.ofNullable(member.getMemberStatus())
                        .ifPresent(memberStatus -> findMember.setMemberStatus(memberStatus));

        return memberRepository.save(findMember);

    }

    public Member findMember(long memberId, CustomUserDetails customUserDetails){

        Member findMember = findVerifiedMember(memberId);

        checkMemberId(findMember.getMemberId(), customUserDetails);

        return findMember;
    }

    public Page<Member> findMembers(int page, int size){
        return memberRepository.findAll(
                PageRequest.of(page, size, Sort.by("memberId").descending())
        );
    }

    public void deleteMember(long memberId, String password, CustomUserDetails customUserDetails){
        Member findMember = findVerifiedMember(memberId);
        checkMemberId(findMember.getMemberId(), customUserDetails);

        if(!passwordEncoder.matches(password, findMember.getPassword())){
            throw new BusinessLogicException(ExceptionCode.MEMBER_PASSWORD_NOT_MATCHED);
        }

        memberRepository.delete(findMember);
    }

    public String renewAccessToken(String refreshToken){

        String base64EncodedSecretKey = jwtTokenizer.encodeBase64SecretKey(jwtTokenizer.getSecretKey());

        int findMemberId = (int) jwtTokenizer.getClaims(refreshToken, base64EncodedSecretKey).getBody().get("memberId");

        Member findMember = findVerifiedMember((long) findMemberId);

        Map<String, Object> claims = new HashMap<>();
        claims.put("memberId", findMember.getMemberId());
        claims.put("username", findMember.getEmail());
        claims.put("roles", findMember.getRoles());

        String subject = findMember.getEmail();
        Date expiration = jwtTokenizer.getTokenExpiration(jwtTokenizer.getAccessTokenExpirationMinutes());

        return "Bearer " + jwtTokenizer.generateAccessToken(claims, subject, expiration, base64EncodedSecretKey);


    }
}
