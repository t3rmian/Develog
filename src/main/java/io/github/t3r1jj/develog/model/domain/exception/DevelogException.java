package io.github.t3r1jj.develog.model.domain.exception;

public class DevelogException extends RuntimeException {
    DevelogException() {
    }

    DevelogException(String message, Throwable cause) {
        super(message, cause);
    }

}
