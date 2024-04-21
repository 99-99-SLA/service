package ro.unibuc.hello.monitoring;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;

@Aspect
@Component
public class DatabaseQueryAspect {

    private final MeterRegistry meterRegistry;

    public DatabaseQueryAspect(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Pointcut("execution(* org.springframework.data.repository.Repository+.*(..))")
    public void repositoryMethods() {}

    @Around("repositoryMethods()")
    public Object profileRepositoryMethod(ProceedingJoinPoint pjp) throws Throwable {
        String methodName = pjp.getSignature().getName();
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            return pjp.proceed();
        } finally {
            sample.stop(Timer.builder("database.query.time")
                    .description("Time taken to execute database query")
                    .tags("method", methodName)
                    .register(meterRegistry));
        }
    }
}

