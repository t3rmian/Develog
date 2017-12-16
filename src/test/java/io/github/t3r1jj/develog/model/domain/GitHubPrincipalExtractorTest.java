package io.github.t3r1jj.develog.model.domain;

import io.github.t3r1jj.develog.model.data.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class GitHubPrincipalExtractorTest {

    @Mock
    private OAuth2User user;
    private final String USER_ATTRIBUTES = "[login=terlecki-rsi, id=25926780, avatar_url=https://avatars1.githubusercontent.com/u/25926780?v=4, gravatar_id=, url=https://api.github.com/users/terlecki-rsi, html_url=https://github.com/terlecki-rsi, followers_url=https://api.github.com/users/terlecki-rsi/followers, following_url=https://api.github.com/users/terlecki-rsi/following{/other_user}, gists_url=https://api.github.com/users/terlecki-rsi/gists{/gist_id}, starred_url=https://api.github.com/users/terlecki-rsi/starred{/owner}{/repo}, subscriptions_url=https://api.github.com/users/terlecki-rsi/subscriptions, organizations_url=https://api.github.com/users/terlecki-rsi/orgs, repos_url=https://api.github.com/users/terlecki-rsi/repos, events_url=https://api.github.com/users/terlecki-rsi/events{/privacy}, received_events_url=https://api.github.com/users/terlecki-rsi/received_events, type=User, site_admin=false, name=testZ, company=null, blog=, location=null, email=terlecki-rsi@mail.com, hireable=null, bio=null, public_repos=0, public_gists=0, followers=0, following=0, created_at=2017-02-21T11:31:05Z, updated_at=2017-12-15T18:49:47Z, private_gists=0, total_private_repos=0, owned_private_repos=0, disk_usage=0, collaborators=0, two_factor_authentication=false, plan={name=free, space=976562499, collaborators=0, private_repos=0}]";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        Map<String, Object> attributes = new HashMap<>();
        when(user.getAttributes()).thenReturn(attributes);
        String[] keyValues = USER_ATTRIBUTES
                .replace("[", "")
                .replace("]", "")
                .replace(" ", "")
                .split(",");
        for (String keyValue : keyValues) {
            String[] kv = keyValue.split("=");
            if (kv.length > 1) {
                attributes.put(kv[0], kv[1]);
            } else {
                attributes.put(kv[0], null);
            }
        }
    }

    @Test
    void extract() {
        GitHubPrincipalExtractor extractor = new GitHubPrincipalExtractor();
        User authenticatedUser = extractor.extract(user);
        assertEquals("terlecki-rsi", authenticatedUser.getName(), "Name should match login");
        assertEquals((Long) 25926780L, authenticatedUser.getId(), "Ids should match");
        assertEquals(User.Role.USER, authenticatedUser.getRole(), "Default role is user");
        assertEquals("terlecki-rsi@mail.com", authenticatedUser.getEmail(), "Emails should match");
    }

    @Test
    void extractId() {
        assertEquals((Long) 25926780L, GitHubPrincipalExtractor.extractId(user));
    }

}