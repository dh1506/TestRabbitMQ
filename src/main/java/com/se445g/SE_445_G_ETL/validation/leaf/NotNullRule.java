package com.se445g.SE_445_G_ETL.validation.leaf;

import com.se445g.SE_445_G_ETL.validation.ValidationResult;
import com.se445g.SE_445_G_ETL.validation.component.ValidationRule;

import java.util.function.Function;

public class NotNullRule<T, F> implements ValidationRule<T> {

  private final String fieldName;
  private final Function<T, F> getter;
  private final String customMessage;

  public NotNullRule(String fieldName, Function<T, F> getter) {
    this.fieldName = fieldName;
    this.getter = getter;
    this.customMessage = String.format("Lỗi Format Rule: Trường '%s' không được để trống.", fieldName);
  }

  public NotNullRule(String fieldName, Function<T, F> getter, String customMessage) {
    this.fieldName = fieldName;
    this.getter = getter;
    this.customMessage = customMessage;
  }

  @Override
  public ValidationResult validate(T data) {
    ValidationResult result = new ValidationResult();
    F value = getter.apply(data);

    // 1. Check NULL (Cơ bản)
    if (value == null) {
      result.addError(customMessage + " (Giá trị là NULL)");
      return result;
    }

    if (value instanceof String) {
      String strValue = (String) value;
      if (strValue.trim().isEmpty()) {
        result.addError(customMessage + " (Giá trị là chuỗi rỗng)");
      }
    }

    return result;
  }
}