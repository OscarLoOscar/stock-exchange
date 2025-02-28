package com.example.coin_exchange.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = SymbolValidator.class) // Links to the validator class
public @interface SymbolCheck {
  
  String message() default "Invalid Symbol. Please use a valid symbol.";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}