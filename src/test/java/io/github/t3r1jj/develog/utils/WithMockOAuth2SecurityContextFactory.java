package io.github.t3r1jj.develog.utils;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithMockOAuth2SecurityContextFactory implements WithSecurityContextFactory<WithMockOAuth2User> {
    @Override
    public SecurityContext createSecurityContext(WithMockOAuth2User customUser) {
        OAuth2User oAuth2User = new DefaultOAuth2User(OAuth2Utils.getDefaultAuthorities(), OAuth2Utils.parseUserDetails(customUser.details()), "login");
        OAuth2AuthenticationToken token = new OAuth2AuthenticationToken(oAuth2User, null, "github");
        token.setAuthenticated(true);
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(token);
        return context;
    }
}