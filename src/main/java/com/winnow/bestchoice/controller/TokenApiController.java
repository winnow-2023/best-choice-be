package com.winnow.bestchoice.controller;

import com.winnow.bestchoice.dto.CreateAccessTokenRequest;
import com.winnow.bestchoice.dto.CreateAccessTokenResponse;
import com.winnow.bestchoice.model.response.TokenResponse;
import com.winnow.bestchoice.service.TokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@Slf4j
public class TokenApiController {
    private final TokenService tokenService;

    @PostMapping("/api/token")
    public ResponseEntity<CreateAccessTokenResponse> createNewAccessToken(@RequestBody CreateAccessTokenRequest request) {
        String newAccessToken = tokenService.createNewAccessToken(request.getRefreshToken());

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CreateAccessTokenResponse(newAccessToken));
    }

    @GetMapping("/token")
    public ResponseEntity<TokenResponse> token(@RequestParam("token") String token, @RequestParam("error") String error) {
        if (!error.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new TokenResponse(error, "FAIL"));
        }

        return ResponseEntity.ok().body(new TokenResponse(token, "OK"));
    }

}
