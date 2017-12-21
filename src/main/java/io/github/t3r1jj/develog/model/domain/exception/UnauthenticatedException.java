package io.github.t3r1jj.develog.model.domain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.TEMPORARY_REDIRECT)
public class UnauthenticatedException extends DevelogException {

    public UnauthenticatedException(String message, Throwable cause) {
        super(message, cause);
    }

}
