package com.mipt.uriilesnikov.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * An aspect for logging service method calls.
 * Uses @Around advice.
 */
@Aspect
@Component
public class LoggingAspect {

    @Around("execution(* com.mipt.uriilesnikov.service.*.*(..))")
    public Object logServiceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().getName();
        System.out.println(" [Aspect] Starting method: " + methodName);
        long start = System.currentTimeMillis();

        Object result;
        try {
            result = joinPoint.proceed();
            System.out.println(" [Aspect] Finished method: " + methodName + " with result: " + result);
        } catch (Throwable e) {
            System.out.println(" [Aspect] Method: " + methodName + " threw exception: " + e.getMessage());
            throw e;
        } finally {
            long duration = System.currentTimeMillis() - start;
            System.out.println(" [Aspect] Execution time: " + duration + "ms");
        }
        return result;
    }
}
