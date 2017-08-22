package com.vivek.audit.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 
 * @author vivek Declaring Annotation Audit Type, which would be used to by
 *         model class, If @Audit annotation is present the model class will be
 *         part of audit process else not.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface Audit {
}
