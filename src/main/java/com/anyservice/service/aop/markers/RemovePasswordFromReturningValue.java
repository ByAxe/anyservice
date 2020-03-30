package com.anyservice.service.aop.markers;

import org.springframework.stereotype.Component;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marker interface for usage in {@link com.anyservice.service.aop.UserAspect}
 * For AOP mechanism to remove password from returning from methods results
 */
@Component
@Target(value = {ElementType.METHOD, ElementType.TYPE})
@Retention(value = RetentionPolicy.RUNTIME)
public @interface RemovePasswordFromReturningValue {
}
