package com.se445g.SE_445_G_ETL.validation.leaf;

import com.se445g.SE_445_G_ETL.validation.ValidationResult;
import com.se445g.SE_445_G_ETL.validation.component.ValidationRule;

public class StringLengthRule<T> implements ValidationRule<T> {

    private final String fieldName;
    private final java.util.function.Function<T, String> getter;
    private final int minLength;
    private final int maxLength;

    public StringLengthRule(String fieldName, java.util.function.Function<T, String> getter, int minLength,
            int maxLength) {
        this.fieldName = fieldName;
        this.getter = getter;
        this.minLength = minLength;
        this.maxLength = maxLength;
    }

    @Override
    public ValidationResult validate(T data) {
        ValidationResult result = new ValidationResult();
        String value = getter.apply(data);

        if (value != null) {
            if (value.length() < minLength || value.length() > maxLength) {
                result.addError(String.format("Lỗi Format Rule: Trường '%s' có độ dài %d không hợp lệ (cần %d-%d).",
                        fieldName, value.length(), minLength, maxLength));
            }
        }
        return result;
    }
}
