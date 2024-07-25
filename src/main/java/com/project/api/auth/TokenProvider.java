package com.project.api.auth;


import com.project.api.metting.entity.Auth;
import com.project.api.metting.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
//토큰을 생성하여 발급하고, 서명 위조를 검사하는 객체
public class TokenProvider {

    //서명에 사용할 512비트의 랜덤 문자열
    @Value("${jwt.secret}")
    private String SECRET_KEY;

    /**
     * JWT를 생성하는 메서드
     * @param user - 토큰에 포함될 로그인한 유저의 정보
     * @return - 생성된 JWT의 암호화된 문자열
     */
    public String createToken(User user) {
          /*
            토큰의 형태
            {
                "iss": "뽀로로월드",
                "exp": "2024-07-18",
                "iat": "2024-07-15",
                ...
                "email": "로그인한 사람 이메일",
                "role": "ADMIN"
                ...
                ===
                서명
            }
         */

        //토큰에 들어갈 커스텀 데이터 ( 추가 클레임 (
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.getEmail());
        claims.put("auth", user.getAuth().toString());
        return Jwts.builder()
                //토큰에 들어갈 서명
                .signWith(
                        Keys.hmacShaKeyFor(SECRET_KEY.getBytes())
                        , SignatureAlgorithm.HS512
                )
                //payload에 들어갈 클레임 설정
                .setClaims(claims) // 추가 클레임은 항상 가장 먼저 설정해야함.
                .setIssuer("meetingProviderKey") // 발급자 정보
                .setIssuedAt(new Date()) // 발그 ㅂ시간
                .setExpiration(Date.from(
                        Instant.now().plus(1, ChronoUnit.DAYS)
                ))// 토큰 만료 시간
                .setSubject(user.getId()) //토큰을 식별할 수 있는 유일한 값
                .compact();

    }


    /**
     * 클라이언트가 전송한 토큰을 디코딩하여 토큰의 서명 위조 여부를 확인
     * 토큰을 JSON 으로 파싱하여 안에 들어있는 클레임(토큰 정보) 를 리턴
     *
     * @param token - 클라이언트가 보낸 토큰
     * @return - 토큰에 들어있는 인증 정보들을 리턴 - 회원 식별 ID
     */
    public TokenUserInfo validateAndGetTokenInfo(String token) {
        //해체할때는 parserBuilder를 사용함.
        Claims claims = Jwts.parserBuilder()
                //토큰 발급자의 발급 당시 서명을 넣음
                .setSigningKey(
                        Keys.hmacShaKeyFor(SECRET_KEY.getBytes())
                )
                //서명 위조 검사 진행 : 위조된 경우 Exception이 발생
                //위조되지 않은 경우 클레임을 리턴시킴

                .build()
                .parseClaimsJws(token)
                .getBody();
        log.info("claims {}", claims);
        //토큰에 인증된 회원의 ID, email, role
        return TokenUserInfo.builder()
                .userId(claims.getSubject())
                .email(claims.get("id", String.class))
                .auth(Auth.valueOf(claims.get("auth", String.class)))
                .build();
    }


    @Getter
    @ToString
    @EqualsAndHashCode
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class TokenUserInfo{
        private String userId;
        private String email;
        private Auth auth;
    }
}
