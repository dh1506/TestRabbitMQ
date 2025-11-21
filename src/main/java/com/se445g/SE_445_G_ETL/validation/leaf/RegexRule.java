package com.se445g.SE_445_G_ETL.validation.leaf;

import com.se445g.SE_445_G_ETL.validation.ValidationResult;
import com.se445g.SE_445_G_ETL.validation.component.ValidationRule;

import java.util.function.Function;
import java.util.regex.Pattern;

public class RegexRule<T> implements ValidationRule<T> {

    private final String fieldName;
    private final Function<T, String> getter;
    private final Pattern pattern;
    private final String customMessage;

    public RegexRule(String fieldName, Function<T, String> getter, String regexPattern, String customMessage) {
        this.fieldName = fieldName;
        this.getter = getter;
        this.pattern = Pattern.compile(regexPattern);
        this.customMessage = customMessage;
    }

    @Override
    public ValidationResult validate(T data) {
        ValidationResult result = new ValidationResult();
        String value = getter.apply(data);

        if (value == null) {
            return result;
        }

      String normalizedValue = value.trim();

        if (!pattern.matcher(normalizedValue).matches()) {
            result.addError(String.format("%s (Giá trị hiện tại: '%s')", customMessage, value));
        }

        return result;
    }

    @Override
    public void setNext(ValidationRule<T> nextHandler) {
        ValidationRule.super.setNext(nextHandler);
    }
}
