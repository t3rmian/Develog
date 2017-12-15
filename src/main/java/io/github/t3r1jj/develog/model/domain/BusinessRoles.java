package io.github.t3r1jj.develog.model.domain;

import io.github.t3r1jj.develog.model.data.User;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface BusinessRoles {
    User.Role[] values();
}
