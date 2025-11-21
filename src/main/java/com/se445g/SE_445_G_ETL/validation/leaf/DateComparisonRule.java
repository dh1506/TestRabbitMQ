package com.se445g.SE_445_G_ETL.validation.leaf;

import com.se445g.SE_445_G_ETL.validation.ValidationResult;
import com.se445g.SE_445_G_ETL.validation.component.ValidationRule;

import java.time.LocalDate;
import java.util.function.Function;

public class DateComparisonRule<T> implements ValidationRule<T> {
    private final Function<T, LocalDate> dateAGetter; // Ví dụ: BirthDate
    private final Function<T, LocalDate> dateBGetter; // Ví dụ: HireDate
    private final String errorMessage;

    public DateComparisonRule(Function<T, LocalDate> dateA, Function<T, LocalDate> dateB, String error) {
        this.dateAGetter = dateA;
        this.dateBGetter = dateB;
        this.errorMessage = error;
    }

    @Override
    public ValidationResult validate(T data) {
        ValidationResult result = new ValidationResult();
        LocalDate dateA = dateAGetter.apply(data);
        LocalDate dateB = dateBGetter.apply(data);

        if (dateA != null && dateB != null && dateA.isAfter(dateB)) {
            result.addError(errorMessage);
        }
        return result;
    }
}
