package com.bhavsar.airBnb.security;

import com.bhavsar.airBnb.dto.LoginDto;
import com.bhavsar.airBnb.dto.SignUpRequestDto;
import com.bhavsar.airBnb.dto.UserDto;
import com.bhavsar.airBnb.model.User;
import com.bhavsar.airBnb.model.enums.Role;
import com.bhavsar.airBnb.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JWTService jwtService;

    public UserDto signUp(SignUpRequestDto signUpRequestDto) {
        User user = userRepository.findByEmail(signUpRequestDto.getEmail()).orElse(null);
        if(user != null) {
            throw new RuntimeException("User already exists with email: " + signUpRequestDto.getEmail());
        }

       User newUser =  modelMapper.map(signUpRequestDto, User.class);
        newUser.setRole(Set.of(Role.ROLE_GUEST));
        newUser.setPassword(passwordEncoder.encode(signUpRequestDto.getPassword()));
        newUser = userRepository.save(newUser);

        return modelMapper.map(newUser , UserDto.class);
    }

    public String [] login(LoginDto loginDto){
        Authentication authenticate = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginDto.getEmail(), loginDto.getPassword()
        ));
        User user = (User) authenticate.getPrincipal();  //Authenticated User

        String [] tokens = new String[2];
        tokens[0] = jwtService.generateAccessToken(user);
        tokens[1] = jwtService.generateRefreshToken(user);
        return tokens;
    }

    public String refreshToken(String refreshToken){
        Long userId = jwtService.getUserIdFromToken(refreshToken);
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new RuntimeException("User not found with id: "+userId));
        return jwtService.generateAccessToken(user);
    }


}
