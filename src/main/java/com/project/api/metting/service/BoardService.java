package com.project.api.metting.service;

import com.project.api.auth.TokenProvider;
import com.project.api.auth.TokenProvider.TokenUserInfo;
import com.project.api.metting.dto.request.BoardRequestDto;
import com.project.api.metting.dto.response.BoardResponseDto;
import com.project.api.metting.entity.Board;
import com.project.api.metting.entity.BoardViewLog;
import com.project.api.metting.entity.User;
import com.project.api.metting.repository.BoardRepository;
import com.project.api.metting.repository.BoardViewLogRepository;
import com.project.api.metting.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.lang.Math.log;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {
    private final BoardRepository boardRepository;
    private final UserRepository userRepository;
    private final BoardViewLogRepository boardViewLogRepository;


    /**
     * 날짜 yyyy.mm.dd 형태로 변환
     *
     * @param dateTime - 로컬데이트타임
     * @return yyyy.mm.dd
     */
    private String convertDateToString(LocalDateTime dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        if (dateTime == null) {
            return null;
        }
        return dateTime.format(formatter);
    }


    @Transactional
    public List<BoardResponseDto> getAllBoards(Pageable pageable) {
        Page<Board> boardPage = boardRepository.findByIsDeletedFalseOrderByCreatedAtDesc(pageable);


        return boardPage.getContent().stream()
                .map(board -> BoardResponseDto.builder()
                        .id(board.getId())
                        .createdAt(convertDateToString(board.getCreatedAt()))
                        .title(board.getTitle())
                        .content(board.getContent())
                        .viewCount(board.getViewCount())
                        .writer(board.getAuthor().getName())
                        .viewCount(board.getViewCount())
                        .imgFile(board.getProfileImgFile())
                        .build()).collect(Collectors.toList());

    }

    public List<BoardResponseDto> getMyBoards(TokenUserInfo tokenUserInfo, Pageable pageable) {
        User user = userRepository.findByEmail(tokenUserInfo.getEmail()).orElseThrow();
        Page<Board> boardList = boardRepository.findByAuthorAndIsDeletedFalseOrderByCreatedAtDesc(pageable, user);
        return boardList.getContent().stream()
                .map(board -> BoardResponseDto.builder()
                .id(board.getId())
                .createdAt(convertDateToString(board.getCreatedAt()))
                .title(board.getTitle())
                .content(board.getContent())
                .viewCount(board.getViewCount())
                .writer(board.getAuthor().getName())
                .viewCount(board.getViewCount())
                .isAuthor(tokenUserInfo.getUserId().equals(board.getAuthor().getId()))
                        .imgFile(board.getProfileImgFile())
                .build()).collect(Collectors.toList());

    }

    /**
     * 조회수 1 늘리기
     *
     * @param board - 보드 정보
     */
    private Board plusOneViewCount(Board board) {
        board.setViewCount(board.getViewCount() + 1);
        boardRepository.save(board);
        log.info("plusOneViewCount - {}", board.toString());
        return board;
    }

    /**
     * 조회 기록 저장 요청
     *
     * @param tokenUserInfo - 유저 정보
     * @param board         - 게시글 정보
     */
    private Board getBoardViewLogAndSetViewCount(TokenUserInfo tokenUserInfo, Board board) {
        User user = userRepository.findById(tokenUserInfo.getUserId()).orElseThrow(() -> new RuntimeException("일치하는 유저 정보가 없습니다."));
        BoardViewLog byBoardIdAndUserId = boardViewLogRepository.findByBoardAndUser(board, user);
        LocalDateTime now = LocalDateTime.now();

        if (byBoardIdAndUserId != null) {
            // 조회했던 유저
            LocalDateTime lastViewedAt = byBoardIdAndUserId.getLastViewedAt();
            long between = ChronoUnit.MINUTES.between(lastViewedAt, now);
            if (between > 30) {
                // 마지막 조회로 부터 30분 경과
                byBoardIdAndUserId.setLastViewedAt(now);
                boardViewLogRepository.save(byBoardIdAndUserId);
                return plusOneViewCount(board);
            }
            return board;
        }
        // 처음 조회하는 유저
        BoardViewLog build = BoardViewLog.builder()
                .user(user)
                .lastViewedAt(now)
                .board(board)
                .build();
        boardViewLogRepository.save(build);
        return plusOneViewCount(board);


    }

    /**
     * 보드 아이디로 보드 디테일 조회
     *
     * @param id - 보드 아이디
     * @return - 보드 dto
     */
    @Transactional
    public BoardResponseDto getBoardById(TokenUserInfo tokenUserInfo, String id) {
        Board board = boardRepository.findById(id).orElseThrow(() -> new RuntimeException("Board not found with id: " + id));
        Board saved = getBoardViewLogAndSetViewCount(tokenUserInfo, board);

//        log.info("getBoardById - {}", saved.toString());
        return BoardResponseDto.builder()
                .id(saved.getId())
                .title(saved.getTitle())
                .content(saved.getContent())
                .writer(saved.getAuthor().getName())
                .viewCount(saved.getViewCount())
                .createdAt(convertDateToString(saved.getCreatedAt()))
                .modifiedAt(convertDateToString(saved.getModifiedAt()))
                .imgFile(board.getProfileImgFile())

                .isAuthor(tokenUserInfo.getUserId().equals(saved.getAuthor().getId()))
                .build();
    }

    /**
     * 보드 생성 요청
     *
     * @param tokenUserInfo   - 유저 정보
     * @param boardRequestDto - 보드 요청 dto
     * @return 보드 dto
     */
    public BoardResponseDto createBoard(TokenUserInfo tokenUserInfo, BoardRequestDto boardRequestDto) {

        User user = userRepository.findById(tokenUserInfo.getUserId()).orElseThrow(() -> new RuntimeException("User not found with id: " + tokenUserInfo.getUserId()));
        Board build = Board.builder().title(boardRequestDto.getTitle()).content(boardRequestDto.getContent()).author(user).build();
        boardRepository.save(build);


        return BoardResponseDto.builder().id(build.getId()).title(build.getTitle()).content(build.getContent()).createdAt(convertDateToString(build.getCreatedAt())).writer(build.getAuthor().getName()).imgFile(build.getProfileImgFile()).build();

    }


    /**
     * 보드 수정 요청
     *
     * @param tokenUserInfo   - 유저 정보
     * @param id              - 보드 아이디
     * @param boardRequestDto - 요청 dto
     * @return - 보드 dto
     */
    public BoardResponseDto modifyBoard(TokenUserInfo tokenUserInfo, String id, BoardRequestDto boardRequestDto) {
        Board board = boardRepository.findById(id).orElseThrow();
        if (!board.getAuthor().getId().equals(tokenUserInfo.getUserId())) {
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

        if (!board.getAuthor().getId().equals(tokenUserInfo.getUserId())) {
            throw new RuntimeException("유저가 작성한 게시글이 아닙니다.");
        }
        board.setIsDeleted(true);
        boardRepository.save(board);

    }
}
