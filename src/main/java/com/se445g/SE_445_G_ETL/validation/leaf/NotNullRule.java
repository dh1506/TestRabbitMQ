package com.se445g.SE_445_G_ETL.validation.leaf;

import com.se445g.SE_445_G_ETL.validation.ValidationResult;
import com.se445g.SE_445_G_ETL.validation.component.ValidationRule;

public class NotNullRule<T, F> implements ValidationRule<T> {

    private final String fieldName;
    private final java.util.function.Function<T, F> getter;

    public NotNullRule(String fieldName, java.util.function.Function<T, F> getter) {
        this.fieldName = fieldName;
        this.getter = getter;
    }

    @Override
    public ValidationResult validate(T data) {
        ValidationResult result = new ValidationResult();
        F value = getter.apply(data);

        if (value == null) {
            result.addError(String.format("Lỗi Format Rule: Trường '%s' không được để trống (NULL).", fieldName));
        }
        return result;
    }
}
