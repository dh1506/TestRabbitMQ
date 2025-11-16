package com.se445g.SE_445_G_ETL.handler;

import com.se445g.SE_445_G_ETL.validation.ValidationResult;
import com.se445g.SE_445_G_ETL.validation.component.ValidationRule;
import com.se445g.SE_445_G_ETL.validation.composite.ValidationRuleGroup;

import lombok.Getter;

@Getter
public abstract class ValidationHandler<T> implements ValidationRule<T> {

    private ValidationRule<T> nextHandler;
    private final ValidationRuleGroup<T> validationRuleGroup; // Composite

    public ValidationHandler(String groupName) {
        this.validationRuleGroup = new ValidationRuleGroup<>(groupName);
    }

    @Override
    public void setNext(ValidationRule<T> nextHandler) {
        this.nextHandler = nextHandler;
    }

    // Template Method cho CoR
    @Override
    public ValidationResult validate(T data) {
        ValidationResult currentResult = validationRuleGroup.validate(data);
        if (nextHandler != null) {
            ValidationResult nextResult = nextHandler.validate(data);
            currentResult.merge(nextResult); // Gộp lỗi
        }

        return currentResult;
    }
    public abstract void buildRules();
}