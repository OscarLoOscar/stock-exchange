package com.example.coin_exchange.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

@Documented
@Constraint(validatedBy = SymbolValidator.class)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface SymbolCheck {
  String message() default "Invalid Symbol.Please use a valid stock symbol.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
