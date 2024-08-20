package com.project.api.metting.service;


import com.project.Main;
import com.project.api.auth.TokenProvider.TokenUserInfo;
import com.project.api.metting.dto.request.BoardRepliesRequestDto;
import com.project.api.metting.dto.response.BoardRepliesResponseDto;
import com.project.api.metting.entity.Board;
import com.project.api.metting.entity.BoardReply;
import com.project.api.metting.entity.User;
import com.project.api.metting.repository.BoardReplyRepository;
import com.project.api.metting.repository.BoardRepository;
import com.project.api.metting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class BoardRepliesService {
    public final BoardReplyRepository boardReplyRepository;
    public final BoardRepository boardRepository;
    public final UserRepository userRepository;


//댓글 GET 매핑 / Page 처리 / 삭제 되지 않는 사람 필터링
    public Page<BoardRepliesResponseDto> getBoardReplies(int pageNo, String boardId) {

        PageRequest pageable = PageRequest.of(pageNo - 1, 5);

        Page<BoardReply> findBoardReply = boardReplyRepository.findByBoardIdAndIsDeletedFalse(pageable,boardId);


       return findBoardReply.map(boardReply ->
                BoardRepliesResponseDto.builder()
                        .id(boardReply.getId())
                        .createdDate(boardReply.getCreatedAt().toLocalDate())
                        .content(boardReply.getContent())
                        .build());



    }

    //댓글 POST / DB에 저장하기
    public void postBoardReplies(BoardRepliesRequestDto dto, TokenUserInfo tokenUserInfo) {

        User user = userRepository.findByEmail(tokenUserInfo.getEmail())
                .orElseThrow(()-> new NoSuchElementException("User not found"));
        Board board = boardRepository.findById(dto.getBoardId())
                .orElseThrow(()-> new NoSuchElementException("board not found"));

        BoardReply reply = BoardReply.builder()
                .content(dto.getContent())
                .author(user)
                .board(board)
                .build();

        boardReplyRepository.save(reply);

    }
}
