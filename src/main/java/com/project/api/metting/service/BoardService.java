package com.project.api.metting.service;

import com.project.api.auth.TokenProvider;
import com.project.api.auth.TokenProvider.TokenUserInfo;
import com.project.api.metting.dto.response.BoardResponseDto;
import com.project.api.metting.entity.Board;
import com.project.api.metting.entity.User;
import com.project.api.metting.repository.BoardRepository;
import com.project.api.metting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;


    private String convertDateToString(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return dateTime.format(formatter);
    }


    public List<BoardResponseDto> getAllBoards() {
        List<Board> boardList = boardRepository.findAll();
        List<Board> collect = boardList.stream().filter(board -> !board.getIsDeleted()).collect(Collectors.toList());
        return  collect.stream().map(board ->
                BoardResponseDto.builder()
                        .id(board.getId())
                        .createdAt(convertDateToString(board.getCreated_at()))
                        .title(board.getTitle())
                        .content(board.getContent())
                        .viewCount(board.getViewCount())
                        .writer(board.getAuthor().getName())
                        .viewCount(board.getViewCount())
                        .build()).collect(Collectors.toList());

    }

    public List<BoardResponseDto> getMyBoards(TokenUserInfo tokenUserInfo) {
        User user = userRepository.findByEmail(tokenUserInfo.getEmail()).orElseThrow();
        List<Board> boardList = boardRepository.findByAuthor(user);
        List<Board> collect = boardList.stream().filter(board -> !board.getIsDeleted()).collect(Collectors.toList());

        return collect.stream().map(board ->
                BoardResponseDto.builder()
                        .id(board.getId())
                        .createdAt(convertDateToString(board.getCreated_at()))
                        .title(board.getTitle())
                        .content(board.getContent())
                        .viewCount(board.getViewCount())
                        .writer(board.getAuthor().getName())
                        .viewCount(board.getViewCount())
                        .build()).collect(Collectors.toList());
    }
}
