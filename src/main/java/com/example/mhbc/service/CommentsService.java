package com.example.mhbc.service;

import com.example.mhbc.dto.CommentsDTO;
import com.example.mhbc.entity.BoardEntity;
import com.example.mhbc.entity.CommentsEntity;
import com.example.mhbc.entity.MemberEntity;
import com.example.mhbc.repository.BoardRepository;
import com.example.mhbc.repository.CommentsRepository;
import com.example.mhbc.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.xml.stream.events.Comment;

@Service
@RequiredArgsConstructor
public class CommentsService {

    private final CommentsRepository commentsRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;

    public void saveComment(CommentsDTO dto, Long memberId) {
        BoardEntity board = boardRepository.findById(dto.getBoardIdx())
                .orElseThrow(() -> new IllegalArgumentException("게시글 없음"));
        MemberEntity member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));

        CommentsEntity comment = new CommentsEntity();
        comment.setContent(dto.getContent());
        comment.setBoard(board);
        comment.setMember(member);

        commentsRepository.save(comment);
    }

    public void deleteComment(long idx) {

        commentsRepository.deleteByIdx(idx);

    }

}
