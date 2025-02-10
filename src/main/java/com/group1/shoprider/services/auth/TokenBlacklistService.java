package com.group1.shoprider.services.auth;


import com.group1.shoprider.models.TokenBlacklist;
import com.group1.shoprider.repository.TokenBlacklistRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TokenBlacklistService {
    private final TokenBlacklistRepository tokenBlacklistRepository;

    public void addToBlacklist(String tokenValue) {
        TokenBlacklist token = new TokenBlacklist();
        token.setValue(tokenValue);
        tokenBlacklistRepository.save(token);
    }

    public boolean isBlacklisted(String tokenValue) {
        Optional<TokenBlacklist> token = tokenBlacklistRepository.findByValue(tokenValue);
        if (token.isPresent()) {
            return true;
        }
        return false;
    }

    public void blacklistToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        String tokenValue = authHeader.substring(7);
        addToBlacklist(tokenValue);
    }
}
