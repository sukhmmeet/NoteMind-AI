package com.dhaliwal.notemind.security.util;

import com.dhaliwal.notemind.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtils {
    public Long getUserId(){
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        assert user != null;
        return user.getId();
    }
}
