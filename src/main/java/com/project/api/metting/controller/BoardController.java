package com.project.api.metting.controller;

import com.project.api.auth.TokenProvider;
import com.project.api.auth.TokenProvider.TokenUserInfo;
import com.project.api.metting.dto.response.BoardResponseDto;
import com.project.api.metting.service.BoardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/board")
@Slf4j
@RequiredArgsConstructor
public class BoardController {
    private final BoardService boardService;

    @GetMapping
    public ResponseEntity<?> getAllBoards(@AuthenticationPrincipal TokenUserInfo tokenUserInfo) {
        List<BoardResponseDto> allBoards = boardService.getAllBoards();

        return ResponseEntity.ok().body(allBoards);
    }

    @GetMapping("/myboards")
    public ResponseEntity<?> getMyBoards(@AuthenticationPrincipal TokenUserInfo tokenUserInfo) {
        List<BoardResponseDto> myBoards = boardService.getMyBoards(tokenUserInfo);
        return ResponseEntity.ok().body(myBoards);
    }

}
