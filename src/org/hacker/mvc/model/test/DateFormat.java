package org.hacker.mvc.model.test;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraints.NotNull;

@NotNull 
@Target({ ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER }) 
@Retention(RetentionPolicy.RUNTIME) 
@Documented
@Constraint(validatedBy = { DateValidator.class }) 
public @interface DateFormat {
	String message() default "error data patten"; 
	Class<?>[] groups() default {};
	Class<? extends Payload>[] payload() default {};
	
	String format();
}