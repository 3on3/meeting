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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
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



    @Transactional
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
        return boardList.stream().map(board ->
                BoardResponseDto.builder()
                        .id(board.getId())
                        .createdAt(convertDateToString(board.getCreatedAt()))
                        .title(board.getTitle())
                        .content(board.getContent())
                        .viewCount(board.getViewCount())
                        .writer(board.getAuthor().getName())
                        .viewCount(board.getViewCount())
                        .isAuthor(tokenUserInfo.getUserId().equals(board.getAuthor().getId()))
                        .build()).collect(Collectors.toList());
    }

    /**
     * 보드 아이디로 보드 디테일 조회
     * @param id - 보드 아이디
     * @return - 보드 dto
     */
    public BoardResponseDto getBoardById(TokenUserInfo tokenUserInfo, String id) {
        Board board = boardRepository.findById(id).orElseThrow(()->new RuntimeException("Board not found with id: " + id));
        return BoardResponseDto.builder().id(board.getId()).title(board.getTitle()).content(board.getContent()).writer(board.getAuthor().getName()).viewCount(board.getViewCount()).createdAt(convertDateToString(board.getCreatedAt())).isAuthor(tokenUserInfo.getUserId().equals(board.getAuthor().getId())).build();
    }

    /**
     * 보드 생성 요청
     * @param tokenUserInfo - 유저 정보
     * @param boardRequestDto - 보드 요청 dto
     * @return 보드 dto
     */
    public BoardResponseDto createBoard(TokenUserInfo tokenUserInfo, BoardRequestDto boardRequestDto) {

        User user = userRepository.findById(tokenUserInfo.getUserId()).orElseThrow(() -> new RuntimeException("User not found with id: " + tokenUserInfo.getUserId()));
        Board build = Board.builder().title(boardRequestDto.getTitle()).content(boardRequestDto.getContent()).author(user).build();
        boardRepository.save(build);

        return BoardResponseDto.builder().id(build.getId()).title(build.getTitle()).content(build.getContent()).createdAt(convertDateToString(build.getCreatedAt())).writer(build.getAuthor().getName()).build();

    }


    public BoardResponseDto modifyBoard(TokenUserInfo tokenUserInfo, String id, BoardRequestDto boardRequestDto) {
        Board board = boardRepository.findById(id).orElseThrow();
       if(!board.getAuthor().getId().equals(tokenUserInfo.getUserId())){
          throw new RuntimeException("Board doesn't exist");
       }
        board.setTitle(boardRequestDto.getTitle());
        board.setContent(boardRequestDto.getContent());
        board.setModifiedAt(LocalDateTime.now());
        boardRepository.save(board);

        return getBoardById(tokenUserInfo, id);
    }

    public void deleteBoard(TokenUserInfo tokenUserInfo, String id) {
        Board board = boardRepository.findById(id).orElseThrow();

        if(!board.getAuthor().getId().equals(tokenUserInfo.getUserId())){
            throw new RuntimeException("유저가 작성한 게시글이 아닙니다.");
        }

        boardRepository.delete(board);

    }
}
