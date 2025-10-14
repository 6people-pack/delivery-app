package com.sparta.delivery.global.audit;

import com.sparta.delivery.security.userdetails.UserDetailsImpl;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<Long> {

    @Override
    public Optional<Long> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // null이 아니고 인증이 되었는지(로그인으로 인한 인증)
        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof UserDetailsImpl userDetails) {
            return Optional.ofNullable(userDetails.getUser().getId());
        }

        return Optional.empty(); // principal이 UserDetailsImpl이 아닌 경우
    }
}