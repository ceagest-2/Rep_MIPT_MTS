package com.mipt.uriilesnikov.validation;

import com.mipt.uriilesnikov.validation.validator.DueDateNotBeforeCreationValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = DueDateNotBeforeCreationValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface DueDateNotBeforeCreation {
    String message() default "Due date cannot be earlier than task creation date";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
