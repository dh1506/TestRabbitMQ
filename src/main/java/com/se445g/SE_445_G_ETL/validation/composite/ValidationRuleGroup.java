package com.se445g.SE_445_G_ETL.validation.composite;

import java.util.ArrayList;
import java.util.List;

import com.se445g.SE_445_G_ETL.validation.ValidationResult;
import com.se445g.SE_445_G_ETL.validation.component.ValidationRule;

public class ValidationRuleGroup<T> implements ValidationRule<T> {

    private final List<ValidationRule<T>> rules = new ArrayList<>();
    @SuppressWarnings("unused")
    private final String groupName;

    public ValidationRuleGroup(String groupName) {
        this.groupName = groupName;
    }

    public void addRule(ValidationRule<T> rule) {
        this.rules.add(rule);
    }

    @Override
    public ValidationResult validate(T data) {
        ValidationResult totalResult = new ValidationResult();
        for (ValidationRule<T> rule : rules) {
            ValidationResult ruleResult = rule.validate(data);
            totalResult.merge(ruleResult);
        }
        return totalResult;
    }
}
