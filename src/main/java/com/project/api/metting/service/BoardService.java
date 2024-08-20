package com.project.api.metting.service;

import com.project.api.auth.TokenProvider;
import com.project.api.auth.TokenProvider.TokenUserInfo;
import com.project.api.metting.dto.request.BoardRequestDto;
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


    /**
     * 날짜 yyyy.mm.dd 형태로 변환
     * @param dateTime - 로컬데이트타임
     * @return yyyy.mm.dd
     */
    private String convertDateToString(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        return dateTime.format(formatter);
    }


    public List<BoardResponseDto> getAllBoards() {
        List<Board> boardList = boardRepository.findByIsDeletedFalseOrderByCreatedAtDesc();
        List<Board> collect = boardList.stream().filter(board -> !board.getIsDeleted()).collect(Collectors.toList());
        return collect.stream().map(board ->
                BoardResponseDto.builder()
                        .id(board.getId())
                        .createdAt(convertDateToString(board.getCreatedAt()))
                        .title(board.getTitle())
                        .content(board.getContent())
                        .viewCount(board.getViewCount())
                        .writer(board.getAuthor().getName())
                        .viewCount(board.getViewCount())
                        .build()).collect(Collectors.toList());

    }

    public List<BoardResponseDto> getMyBoards(TokenUserInfo tokenUserInfo) {
        User user = userRepository.findByEmail(tokenUserInfo.getEmail()).orElseThrow();
        List<Board> boardList = boardRepository.findByAuthorAndIsDeletedFalseOrderByCreatedAtDesc(user);
        List<Board> collect = boardList.stream().filter(board -> !board.getIsDeleted()).collect(Collectors.toList());

        return collect.stream().map(board ->
                BoardResponseDto.builder()
                        .id(board.getId())
                        .createdAt(convertDateToString(board.getCreatedAt()))
                        .title(board.getTitle())
                        .content(board.getContent())
                        .viewCount(board.getViewCount())
                        .writer(board.getAuthor().getName())
                        .viewCount(board.getViewCount())
                        .build()).collect(Collectors.toList());
    }

    public BoardResponseDto getBoardById(String id) {
        Board board = boardRepository.findById(id).orElseThrow(()->new RuntimeException("Board not found with id: " + id));
        return BoardResponseDto.builder().id(board.getId()).title(board.getTitle()).content(board.getContent()).writer(board.getAuthor().getName()).viewCount(board.getViewCount()).createdAt(convertDateToString(board.getCreatedAt())).build();
    }

    public BoardResponseDto createBoard(TokenUserInfo tokenUserInfo, BoardRequestDto boardRequestDto) {

        User user = userRepository.findById(tokenUserInfo.getUserId()).orElseThrow(() -> new RuntimeException("User not found with id: " + tokenUserInfo.getUserId()));
        Board build = Board.builder().title(boardRequestDto.getTitle()).content(boardRequestDto.getContent()).author(user).build();
        boardRepository.save(build);

        return BoardResponseDto.builder().id(build.getId()).title(build.getTitle()).content(build.getContent()).createdAt(convertDateToString(build.getCreatedAt())).writer(build.getAuthor().getName()).build();

    }
}
