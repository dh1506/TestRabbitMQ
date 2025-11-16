package com.se445g.SE_445_G_ETL.validation.component;

import com.se445g.SE_445_G_ETL.validation.ValidationResult;

public interface ValidationRule<T> {
    ValidationResult validate(T data);

    default void setNext(ValidationRule<T> nextHandler) {
    }
}
