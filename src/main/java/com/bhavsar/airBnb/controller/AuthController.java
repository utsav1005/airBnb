package com.bhavsar.airBnb.controller;

import com.bhavsar.airBnb.dto.LoginDto;
import com.bhavsar.airBnb.dto.LoginResponseDto;
import com.bhavsar.airBnb.dto.SignUpRequestDto;
import com.bhavsar.airBnb.dto.UserDto;
import com.bhavsar.airBnb.security.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<UserDto> signupUser(@Valid @RequestBody SignUpRequestDto signUpRequestDto){
        return new ResponseEntity<>(authService.signUp(signUpRequestDto) , HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> loginUser(
            @RequestBody LoginDto loginDto , HttpServletRequest httpServletRequest , HttpServletResponse httpServletResponse){
        String[] tokens = authService.login(loginDto);

        Cookie cookie = new Cookie("refreshToken",tokens[1]);
        cookie.setHttpOnly(true); //Javascript cannot access this cookie
        cookie.setMaxAge(60*60*24*7);  //7Days
        cookie.setAttribute("SameSite" , "None");
        cookie.setPath("/");
        httpServletResponse.addCookie(cookie);
        return ResponseEntity.ok(new LoginResponseDto(tokens[0]));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDto> refreshToken(HttpServletRequest request){
        String refreshToken = Arrays.stream(request.getCookies())
                .filter(cookie -> cookie.getName().equals("refreshToken"))
                .findFirst()
                .map(Cookie ::getValue)
                .orElseThrow(() -> new AuthenticationServiceException("Refresh token not found in cookies"));
        String accessToken = authService.refreshToken(refreshToken);
        return ResponseEntity.ok(new LoginResponseDto(accessToken));


    }

}
