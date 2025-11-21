package com.se445g.SE_445_G_ETL.validation.leaf;

import com.se445g.SE_445_G_ETL.validation.ValidationResult;
import com.se445g.SE_445_G_ETL.validation.component.ValidationRule;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

public class AllowedValuesRule<T> implements ValidationRule<T> {

  private final String fieldName;
  private final Function<T, String> getter;
  private final Set<String> allowedValues;
  private String customMessage;

  @SafeVarargs // An toàn khi dùng varargs
  public AllowedValuesRule(String fieldName, Function<T, String> getter, String... values) {
    this.fieldName = fieldName;
    this.getter = getter;
    this.allowedValues = Arrays.stream(values)
      .map(String::trim)
      .collect(Collectors.toSet());

    this.customMessage = "Giá trị không nằm trong danh sách cho phép: " + String.join(", ", values);
  }

  public AllowedValuesRule<T> withMessage(String message) {
    this.customMessage = message;
    return this;
  }

  @Override
  public ValidationResult validate(T data) {
    ValidationResult result = new ValidationResult();
    String rawValue = getter.apply(data);

    if (rawValue == null) {
      return result;
    }

    String normalizedInput = rawValue.trim();

    boolean isValid = allowedValues.stream()
      .anyMatch(allowed -> allowed.equalsIgnoreCase(normalizedInput));

    if (!isValid) {
      result.addError(String.format("[%s] %s (Giá trị hiện tại: '%s')",
        fieldName, customMessage, rawValue));
    }

    return result;
  }
}