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
import java.util.Date;

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

    public void deleteComment(Long idx) {

        commentsRepository.deleteByIdx(idx);

    }


    public void modifyComment(Long idx, String content){
        CommentsEntity comment = commentsRepository.findById(idx)
                .orElseThrow(() -> new IllegalArgumentException("댓글이 존재하지 않습니다."));
        comment.setIdx(idx);
        comment.setContent(content);
        comment.setUpdatedAt(new Date());
        commentsRepository.save(comment);
    }
}
