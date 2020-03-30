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

    /**
     * Remove password from returning from methods results,
     * annotated with {@link com.anyservice.service.aop.markers.RemovePasswordFromReturningValue}
     *
     * @param proceedingJoinPoint special object that allows us to catch calls of watched methods
     * @return result of execution of target methods, without password
     * @throws Throwable if something goes wrong in target methods, throw the exception up
     */
    @Around("@annotation(com.anyservice.service.aop.markers.RemovePasswordFromReturningValue) " +
            "&& within(com.anyservice.service.user.UserService)")
    public Object removePasswordFromReturningValue(final ProceedingJoinPoint proceedingJoinPoint)
            throws Throwable {

        // Allow target method to do its job
        UserDetailed user = (UserDetailed) proceedingJoinPoint.proceed();

        // Remove password from returning result
        if (user != null) {
            user.setPassword(null);
        }

        // Return user, as if there is nothing happened
        return user;
    }

}
