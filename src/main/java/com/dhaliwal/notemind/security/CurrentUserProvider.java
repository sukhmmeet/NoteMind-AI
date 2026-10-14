package com.dhaliwal.notemind.security;

import com.dhaliwal.notemind.entity.User;
import com.dhaliwal.notemind.exception.UserNotLoggedInException;
import com.dhaliwal.notemind.repository.UserRepository;
import com.dhaliwal.notemind.security.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CurrentUserProvider {

    private final SecurityUtils securityUtils;
    private final UserRepository userRepository;

    public User getCurrentUser() {
        Long userId = securityUtils.getUserId();

        if (userId == null) {
            throw new UserNotLoggedInException("User not logged in");
        }

        return userRepository.findById(userId)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));
    }
}
