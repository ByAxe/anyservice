package com.anyservice.dto.api;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Markup annotation
 * <p>
 * Describes that this class is a Brief version of a real object.
 * <p>
 * This class should be used in lists and wherever not needed detailed data about real object
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.CLASS)
public @interface Brief {
}
