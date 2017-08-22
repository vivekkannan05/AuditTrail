package com.vivek.audit.model;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declaring Annotation NOAudit Type, which would be used by element of the
 * modelclass, If @NoAudit annotation is present on the element of the @Audit
 * model class it would be skipped from audit process.
 * 
 * @author vivek
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface NoAudit {

}
