package com.se445g.SE_445_G_ETL.validation.leaf;

import com.se445g.SE_445_G_ETL.validation.ValidationResult;
import com.se445g.SE_445_G_ETL.validation.component.ValidationRule;

import java.util.function.Function;

public class NumericRangeRule<T> implements ValidationRule<T> {
    private final String fieldName;
    private final Function<T, ? extends Number> getter;
    private final double min;
    private final double max;

    public NumericRangeRule(String fieldName, Function<T, ? extends Number> getter, double min, double max) {
        this.fieldName = fieldName;
        this.getter = getter;
        this.min = min;
        this.max = max;
    }

    @Override
    public ValidationResult validate(T data) {
        ValidationResult result = new ValidationResult();
        Number val = getter.apply(data);

        if (val == null) return result; // Null check để rule khác lo

        double doubleVal = val.doubleValue();
        if (doubleVal < min || doubleVal > max) {
            result.addError(String.format("Trường '%s' giá trị %.2f nằm ngoài phạm vi cho phép [%.2f - %.2f]",
                    fieldName, doubleVal, min, max));
        }
        return result;
    }
}
