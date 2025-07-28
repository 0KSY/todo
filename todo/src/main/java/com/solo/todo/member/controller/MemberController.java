package com.solo.todo.member.controller;

import com.solo.todo.dto.MultiResponseDto;
import com.solo.todo.dto.SingleResponseDto;
import com.solo.todo.member.dto.MemberDto;
import com.solo.todo.member.entity.Member;
import com.solo.todo.member.mapper.MemberMapper;
import com.solo.todo.member.service.MemberService;
import com.solo.todo.utils.UriCreator;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/members")
public class MemberController {

    private final MemberService memberService;
    private final MemberMapper mapper;
    private static final String MEMBER_DEFAULT_URL = "/members";

    public MemberController(MemberService memberService, MemberMapper mapper) {
        this.memberService = memberService;
        this.mapper = mapper;
    }

    @PostMapping
    public ResponseEntity postMember(@RequestBody MemberDto.Post memberPostDto){

        Member member = memberService.createMember(mapper.memberPostDtoToMember(memberPostDto));

        URI location = UriCreator.createUri(MEMBER_DEFAULT_URL, member.getMemberId());

        return ResponseEntity.created(location).build();

    }

    @PatchMapping("/{member-id}")
    public ResponseEntity patchMember(@RequestBody MemberDto.Patch memberPatchDto,
                                      @PathVariable("member-id") long memberId){

        memberPatchDto.setMemberId(memberId);

        Member member = memberService.updateMember(mapper.memberPatchDtoToMember(memberPatchDto));

        return new ResponseEntity(new SingleResponseDto<>(mapper.memberToMemberResponseDto(member)), HttpStatus.OK);

    }

    @GetMapping("/{member-id}")
    public ResponseEntity getMember(@PathVariable("member-id") long memberId){

        Member member = memberService.findMember(memberId);

        return new ResponseEntity(new SingleResponseDto<>(mapper.memberToMemberResponseDto(member)), HttpStatus.OK);

    }

    @GetMapping
    public ResponseEntity getMembers(@RequestParam int page,
                                     @RequestParam int size){

        Page<Member> pageMembers = memberService.findMembers(page-1, size);
        List<Member> members = pageMembers.getContent();

        return new ResponseEntity(
                new MultiResponseDto<>(mapper.membersToMemberResponseDtos(members), pageMembers), HttpStatus.OK);


    }

    @DeleteMapping("/{member-id}")
    public ResponseEntity deleteMember(@PathVariable("member-id") long memberId){

        memberService.deleteMember(memberId);

        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

}
