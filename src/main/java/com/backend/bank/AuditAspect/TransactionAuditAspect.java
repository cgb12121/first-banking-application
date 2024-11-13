package com.backend.bank.AuditAspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TransactionAuditAspect {
    @Around("execution(* com.backend.bank.service.TransactionService.*(..))")
    public Object auditTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
        // Log transaction details
        Object result = joinPoint.proceed();
        return result;
    }
}
