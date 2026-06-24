package com.dhaliwal.notemind.security;

import com.dhaliwal.notemind.entity.User;
import com.dhaliwal.notemind.security.dto.RequestDto;
import com.dhaliwal.notemind.security.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthUtil authUtil;


    public ResponseDto signup(RequestDto requestDto) {

        if (userRepository.findByUsername(requestDto.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User user = new User();
        user.setUsername(requestDto.getUsername());
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));

        User savedUser = userRepository.save(user);

        String token = authUtil.generateAccessToken(savedUser);

        ResponseDto response = new ResponseDto();
        response.setUsername(savedUser.getUsername());
        response.setUserId(savedUser.getId());
        response.setJwtToken(token);

        return response;
    }


    public ResponseDto login(RequestDto requestDto) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                requestDto.getUsername(),
                                requestDto.getPassword()
                        )
                );


        User user = (User) userRepository.findByUsername(requestDto.getUsername())
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found")
                );


        String token = authUtil.generateAccessToken(user);


        ResponseDto response = new ResponseDto();
        response.setUsername(user.getUsername());
        response.setUserId(user.getId());
        response.setJwtToken(token);

        return response;
    }
}