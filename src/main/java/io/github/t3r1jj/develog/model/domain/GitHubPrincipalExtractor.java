package io.github.t3r1jj.develog.model.domain;

import io.github.t3r1jj.develog.model.data.User;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class GitHubPrincipalExtractor {
    private static final Set<String> admins = new HashSet<String>() {{
        this.add("T3r1jj");
    }};

    public User extract(OAuth2User principal) {
        Map<String, Object> attributes = principal.getAttributes();
        return User.builder()
                .id(Long.valueOf(attributes.get("id").toString()))
                .email((String) attributes.get("email"))
                .name((String) attributes.get("login"))
                .role(admins.contains(attributes.get("login")) ? User.Role.ADMIN : User.Role.USER)
                .build();
    }

    public static Long extractId(OAuth2User principal) {
        return Long.valueOf(principal.getAttributes().get("id").toString());
    }
}
