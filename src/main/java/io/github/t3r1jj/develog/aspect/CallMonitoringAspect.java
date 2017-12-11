package io.github.t3r1jj.develog.aspect;

import io.github.t3r1jj.develog.model.monitoring.Call;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

import java.util.HashMap;

import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

@Aspect
@Component
@Scope(value = SCOPE_SINGLETON)
public class CallMonitoringAspect {

    private static final Logger logger = LoggerFactory.getLogger(CallMonitoringAspect.class);

    private HashMap<String, Call> logs = new HashMap<>();

    private boolean isEnabled;

    public CallMonitoringAspect(@Value("${io.github.t3r1jj.develog.monitoring.call.enabled}") boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public boolean isEnabled() {
        return isEnabled;
    }

    public void reset() {
        logs.clear();
    }

    @Around("within(@org.springframework.stereotype.Service *)")
    public Object invoke(ProceedingJoinPoint joinPoint) throws Throwable {
        if (this.isEnabled) {
            StopWatch sw = new StopWatch(joinPoint.toShortString());
            sw.start("invoke");
            try {
                if (logger.isInfoEnabled()) {
                    logger.info("service call {}.{}",
                            joinPoint.getSignature().getDeclaringTypeName(),
                            joinPoint.getSignature().getName());
                }
                return joinPoint.proceed();
            } finally {
                sw.stop();
                log(joinPoint.getSignature().getDeclaringTypeName() + "." +
                        joinPoint.getSignature().getName(), sw.getTotalTimeMillis());
            }
        } else {
            return joinPoint.proceed();
        }
    }

    private synchronized void log(String name, long callTime) {
        Call call = logs.get(name);
        if (call == null) {
            call = new Call(name);
        }
        call.accumulateCall(callTime);
        logs.put(name, call);
    }

    public HashMap<String, Call> getLogs() {
        return logs;
    }

}
