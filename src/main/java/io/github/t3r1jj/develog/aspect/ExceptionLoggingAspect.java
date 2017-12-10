package io.github.t3r1jj.develog.aspect;

import io.github.t3r1jj.develog.model.monitoring.ErrorInfo;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.io.PrintWriter;
import java.io.StringWriter;

@Aspect
@Component
public class ExceptionLoggingAspect {

    @AfterThrowing(pointcut = "within(@org.springframework.stereotype.Service *)", throwing = "error")
    public void logAfterError(JoinPoint joinPoint, Throwable error) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter printWriter = new PrintWriter(stringWriter);
        error.printStackTrace(printWriter);
        String stackTrace = stringWriter.toString();
        new ErrorInfo(joinPoint.getSignature().getDeclaringTypeName() + "." +
                joinPoint.getSignature().getName(), stackTrace, error.getMessage());

    }

}
