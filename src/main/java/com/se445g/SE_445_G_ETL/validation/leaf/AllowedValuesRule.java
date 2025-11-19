package com.se445g.SE_445_G_ETL.validation.leaf;

import com.se445g.SE_445_G_ETL.validation.ValidationResult;
import com.se445g.SE_445_G_ETL.validation.component.ValidationRule;

import java.util.Set;
import java.util.function.Function;

public class AllowedValuesRule<T> implements ValidationRule<T> {

    private final String fieldName;
    private final Function<T, String> getter;
    private final Set<String> allowedValues; // Dùng Set để search nhanh

    public AllowedValuesRule(String fieldName, Function<T, String> getter, Set<String> allowedValues) {
        this.fieldName = fieldName;
        this.getter = getter;
        this.allowedValues = allowedValues;
    }

    // Constructor phụ dùng VarArgs cho tiện (truyền string liệt kê)
    public AllowedValuesRule(String fieldName, Function<T, String> getter, String... values) {
        this.fieldName = fieldName;
        this.getter = getter;
        this.allowedValues = Set.of(values); // Java 9+
    }

    @Override
    public ValidationResult validate(T data) {
        ValidationResult result = new ValidationResult();
        String value = getter.apply(data);

        // Null check để rule khác lo, hoặc tùy logic của bạn
        if (value == null) return result;

        if (!allowedValues.contains(value)) {
            result.addError(String.format("Trường '%s' có giá trị '%s' không nằm trong danh sách cho phép: %s",
                    fieldName, value, allowedValues));
        }
        return result;
    }
}
