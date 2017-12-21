package io.github.t3r1jj.develog.utils;


import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithMockOAuth2SecurityContextFactory.class)
public @interface WithMockOAuth2User {

    String details() default "[login=terlecki-rsi, id=25926780, avatar_url=https://avatars1.githubusercontent.com/u/25926780?v=4, gravatar_id=, url=https://api.github.com/users/terlecki-rsi, html_url=https://github.com/terlecki-rsi, followers_url=https://api.github.com/users/terlecki-rsi/followers, following_url=https://api.github.com/users/terlecki-rsi/following{/other_user}, gists_url=https://api.github.com/users/terlecki-rsi/gists{/gist_id}, starred_url=https://api.github.com/users/terlecki-rsi/starred{/owner}{/repo}, subscriptions_url=https://api.github.com/users/terlecki-rsi/subscriptions, organizations_url=https://api.github.com/users/terlecki-rsi/orgs, repos_url=https://api.github.com/users/terlecki-rsi/repos, events_url=https://api.github.com/users/terlecki-rsi/events{/privacy}, received_events_url=https://api.github.com/users/terlecki-rsi/received_events, type=User, site_admin=false, name=testZ, company=null, blog=, location=null, email=terlecki-rsi@mail.com, hireable=null, bio=null, public_repos=0, public_gists=0, followers=0, following=0, created_at=2017-02-21T11:31:05Z, updated_at=2017-12-15T18:49:47Z, private_gists=0, total_private_repos=0, owned_private_repos=0, disk_usage=0, collaborators=0, two_factor_authentication=false, plan={name=free, space=976562499, collaborators=0, private_repos=0}]";

}