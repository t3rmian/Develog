package io.github.t3r1jj.develog.model.domain.exception;

public class DevelogException extends RuntimeException {
    DevelogException() {
    }

    DevelogException(String message) {
        super(message);
    }

    DevelogException(String message, Throwable cause) {
        super(message, cause);
    }

    DevelogException(Throwable cause) {
        super(cause);
    }

    DevelogException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
