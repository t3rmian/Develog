package io.github.t3r1jj.develog.aspect;

import io.github.t3r1jj.develog.model.monitor.Error;
import io.github.t3r1jj.develog.repository.monitoring.ErrorRepository;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;

@Aspect
@Component
public class ExceptionLoggingAspect {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionLoggingAspect.class);
    private final ErrorRepository errorRepository;

    @Autowired
    public ExceptionLoggingAspect(ErrorRepository errorRepository) {
        this.errorRepository = errorRepository;
    }

    @AfterThrowing(pointcut = "within(@org.springframework.stereotype.Service *)", throwing = "error")
    public void logAfterError(JoinPoint joinPoint, Throwable error) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        error.printStackTrace(printWriter);
        String stackTrace = stringWriter.toString();
        Error errorInfo = new Error(joinPoint.getSignature().getDeclaringTypeName() + "." +
                joinPoint.getSignature().getName(), stackTrace, error.getMessage());
        if (logger.isErrorEnabled()) {
            logger.error("service call {}.{}, exception {}",
                    joinPoint.getSignature().getDeclaringTypeName(),
                    joinPoint.getSignature().getName(), stackTrace);
        }
        errorRepository.save(errorInfo);
    }
}
