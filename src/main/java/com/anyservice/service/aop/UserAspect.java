package com.anyservice.service.aop;

import com.anyservice.dto.user.UserDetailed;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Log4j2
public class UserAspect {

    @Around("@annotation(com.anyservice.service.aop.markers.RemovePasswordFromReturningValue) " +
            "&& within(com.anyservice.service.user.UserService)")
    public Object removePasswordFromReturningValue(final ProceedingJoinPoint proceedingJoinPoint)
            throws Throwable {
        UserDetailed user = (UserDetailed) proceedingJoinPoint.proceed();

        user.setPassword(null);

        return user;
    }

}
