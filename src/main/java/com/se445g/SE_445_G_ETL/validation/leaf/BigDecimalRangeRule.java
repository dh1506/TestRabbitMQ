package com.se445g.SE_445_G_ETL.validation.leaf;

import com.se445g.SE_445_G_ETL.validation.ValidationResult;
import com.se445g.SE_445_G_ETL.validation.component.ValidationRule;

import java.math.BigDecimal;
import java.util.function.Function;

public class BigDecimalRangeRule<T> implements ValidationRule<T> {
    private final String fieldName;
    private final Function<T, BigDecimal> getter;
    private final BigDecimal min;
    private final BigDecimal max;

    public BigDecimalRangeRule(String fieldName, Function<T, BigDecimal> getter, String minStr, String maxStr) {
        this.fieldName = fieldName;
        this.getter = getter;
        this.min = new BigDecimal(minStr); // Dùng String constructor để chính xác tuyệt đối
        this.max = new BigDecimal(maxStr);
    }

    @Override
    public ValidationResult validate(T data) {
        ValidationResult result = new ValidationResult();
        BigDecimal value = getter.apply(data);

        if (value == null) return result; // Null để rule khác lo

        // compareTo trả về: -1 (nhỏ hơn), 0 (bằng), 1 (lớn hơn)
        if (value.compareTo(min) < 0 || value.compareTo(max) > 0) {
            result.addError(String.format("Trường '%s' giá trị %s nằm ngoài khoảng cho phép [%s - %s]",
                    fieldName, value.toPlainString(), min.toPlainString(), max.toPlainString()));
        }
        return result;
    }
}
