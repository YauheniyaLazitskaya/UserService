package com.services;

import com.entities.User;
import com.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

@Service("securityCheck")
@RequiredArgsConstructor
public class SecurityCheckService {
    private final UserRepository userRepository;

    public boolean isUserOwner(Integer requestedUserId, Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated())
            return false;

        String tokenKeycloakId = authentication.getName();
        if (authentication.getPrincipal() instanceof Jwt jwt)
            tokenKeycloakId = jwt.getClaimAsString("sub");

        String finalTokenKeycloakId = tokenKeycloakId;
        return userRepository.findById(requestedUserId)
                .map(user -> user.getKeycloakId().equals(finalTokenKeycloakId))
                .orElse(false);
    }
}
