package com.project.api.metting.service;


import com.project.api.auth.TokenProvider.TokenUserInfo;
import com.project.api.metting.dto.request.BoardRepliesRequestDto;
import com.project.api.metting.dto.request.ReplyDeletRequestDto;
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
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.format.DateTimeFormatter;
import java.util.NoSuchElementException;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class BoardRepliesService {
    public final BoardReplyRepository boardReplyRepository;
    public final BoardRepository boardRepository;
    public final UserRepository userRepository;


    //댓글 GET 매핑 / Page 처리 / 삭제 되지 않는 사람 필터링
    public Page<BoardRepliesResponseDto> getBoardReplies(int pageNo, String boardId, TokenUserInfo tokenUserInfo) {

        PageRequest pageable = PageRequest.of(pageNo - 1, 5, Sort.by(Sort.Direction.DESC, "createdAt"));


        Page<BoardReply> findBoardReply = boardReplyRepository.findByBoardIdAndIsDeletedFalse(pageable,boardId);



        return findBoardReply.map(boardReply ->
                BoardRepliesResponseDto.builder()
                        .id(boardReply.getId())
                        .createdDate(boardReply.getCreatedAt().format(dateTimeFormatter()))
                        .content(boardReply.getContent())
                        .isAuthor(tokenUserInfo.getUserId().equals(boardReply.getAuthor().getId()))
                        .build());



    }

    //날짜 Formatter
    public DateTimeFormatter dateTimeFormatter(){
       DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("MM/dd HH:mm");
        return dateTimeFormatter;
    }

    //댓글 POST / DB에 저장하기
    public BoardRepliesResponseDto postBoardReplies(BoardRepliesRequestDto dto, TokenUserInfo tokenUserInfo) {

        User user = userRepository.findByEmail(tokenUserInfo.getEmail())
                .orElseThrow(()-> new NoSuchElementException("User not found"));
        Board board = boardRepository.findById(dto.getBoardId())
                .orElseThrow(()-> new NoSuchElementException("board not found"));

        BoardReply reply = BoardReply.builder()
                .content(dto.getContent())
                .author(user)
                .board(board)
                .build();


        BoardReply saveBoardReply = boardReplyRepository.save(reply);

        BoardRepliesResponseDto repliesDto = BoardRepliesResponseDto.builder()
                .content(saveBoardReply.getContent())
                .id(saveBoardReply.getId())
                .createdDate(saveBoardReply.getCreatedAt().format(dateTimeFormatter()))
                .isAuthor(tokenUserInfo.getUserId().equals(saveBoardReply.getAuthor().getId()))
                .build();


        return repliesDto;

    }

    public void deleteBoardReply(ReplyDeletRequestDto dto) {
//        boardReplyRepository.deleteById(boardId);

        //댓글 찾기
        BoardReply boardReply = boardReplyRepository.findById(dto.getReplyId())
                .orElseThrow(()-> new NoSuchElementException("reply not found"));



        // 해당 댓글 삭제
        boardReply.setIsDeleted(true);

        //디비에 저장
        boardReplyRepository.save(boardReply);

    }



}
