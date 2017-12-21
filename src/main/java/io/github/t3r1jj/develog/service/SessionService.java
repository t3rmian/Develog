package io.github.t3r1jj.develog.service;

import io.github.t3r1jj.develog.model.data.User;
import io.github.t3r1jj.develog.model.domain.GitHubPrincipalExtractor;
import io.github.t3r1jj.develog.model.domain.exception.UnauthenticatedException;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
class SessionService {
    User getAuthenticatedUser() {
        try {
            OAuth2AuthenticationToken token = getOAuth2Token();
            return new GitHubPrincipalExtractor().extract(token.getPrincipal());
        } catch (RuntimeException ex) {
            throw new UnauthenticatedException("Could not get authenticated user details", ex);
        }
    }

    Long getAuthenticatedUserId() {
        try {
            OAuth2AuthenticationToken token = getOAuth2Token();
            return GitHubPrincipalExtractor.extractId(token.getPrincipal());
        } catch (RuntimeException ex) {
            throw new UnauthenticatedException("Could not get authenticated user details", ex);
        }
    }

    private OAuth2AuthenticationToken getOAuth2Token() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (OAuth2AuthenticationToken) authentication;
    }

    boolean isSessionAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        AbstractAuthenticationToken token = (AbstractAuthenticationToken) authentication;
        return token.isAuthenticated() && !Objects.equals("anonymousUser", token.getPrincipal());
    }

}
