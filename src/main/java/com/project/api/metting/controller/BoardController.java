package com.project.api.metting.controller;

import com.project.api.auth.TokenProvider;
import com.project.api.auth.TokenProvider.TokenUserInfo;
import com.project.api.metting.dto.request.BoardRequestDto;
import com.project.api.metting.dto.response.BoardResponseDto;
import com.project.api.metting.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/board")
@Slf4j
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    /**
     * 모든 게시글 조회
     * @param tokenUserInfo - 유저정보
     * @return 모든 게시글 dto
     */
    @GetMapping
    public ResponseEntity<?> getAllBoards(@AuthenticationPrincipal TokenUserInfo tokenUserInfo) {
        List<BoardResponseDto> allBoards = boardService.getAllBoards();

        return ResponseEntity.ok().body(allBoards);
    }

    /**
     * 유저가 작성한 게시글만 조회
     * @param tokenUserInfo - 유저정보
     * @return 유저가 작성한 게시글 dto
     */
    @GetMapping("/myboards")
    public ResponseEntity<?> getMyBoards(@AuthenticationPrincipal TokenUserInfo tokenUserInfo) {
        List<BoardResponseDto> myBoards = boardService.getMyBoards(tokenUserInfo);
        return ResponseEntity.ok().body(myBoards);
    }

    /**
     * 게시판 상세페이지 조회
     * @param tokenUserInfo - 유저정보
     * @param id - 게시판 고유 아이디
     * @return 게시판 dto
     */
    @GetMapping("/detail/{id}")
    public ResponseEntity<?> getBoardDetail(@AuthenticationPrincipal TokenUserInfo tokenUserInfo, @PathVariable String id) {
        try {
            BoardResponseDto responseDto = boardService.getBoardById(tokenUserInfo,id);
            log.info("responseDto - {}",responseDto.toString());
            return ResponseEntity.ok().body(responseDto);

        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }

    }

    /**
     * 새 게시글 작성 요청
     * @param tokenUserInfo - 유저 정보
     * @param boardRequestDto - 새 게시글 정보
     * @return 새 게시글 dto
     */
    @PostMapping("/create")
    public ResponseEntity<?> createBoard(@AuthenticationPrincipal TokenUserInfo tokenUserInfo, @RequestBody BoardRequestDto boardRequestDto) {

        try {
            BoardResponseDto board = boardService.createBoard(tokenUserInfo, boardRequestDto);
            return ResponseEntity.ok(board);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    /**
     * 게시글 수정 페이지 요청
     * @param tokenUserInfo - 유저정보
     * @param id - 게시글 아이디
     * @return - 수정 전 게시글 dto
     */
    @GetMapping("/modify/{id}")
    public ResponseEntity<?> modifyBoard(@AuthenticationPrincipal TokenUserInfo tokenUserInfo, @PathVariable String id){
        try {
            BoardResponseDto responseDto = boardService.getBoardById(tokenUserInfo,id);
            return ResponseEntity.ok().body(responseDto);
        } catch (RuntimeException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An unexpected error occurred");
        }
    }

    /**
     * 게시글 수정 요청
     * @param tokenUserInfo - 유저정보
     * @param id - 게시글 아이디
     * @return - 수정 후 게시글 dto
     */
    @PostMapping("/modify/{id}")
    public ResponseEntity<?> modifyBoard(@AuthenticationPrincipal TokenUserInfo tokenUserInfo, @PathVariable String id, @RequestBody BoardRequestDto boardRequestDto){
        BoardResponseDto responseDto = boardService.modifyBoard(tokenUserInfo, id, boardRequestDto);
        return ResponseEntity.ok().body(responseDto);
    }


    /**
     * 게시글 삭제 요청
     * @param tokenUserInfo - 유저정보
     * @param id - 게시글 아이디
     * @return - 성공메세지
     */
    @PostMapping("/delete/{id}")
    public ResponseEntity<?> deleteBoard(@AuthenticationPrincipal TokenUserInfo tokenUserInfo, @PathVariable String id){
        boardService.deleteBoard(tokenUserInfo,id);
        return ResponseEntity.ok().body("게시글 삭제 완료되었습니다.");
    }

}
