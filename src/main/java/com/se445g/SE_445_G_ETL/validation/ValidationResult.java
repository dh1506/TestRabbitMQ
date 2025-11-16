package com.se445g.SE_445_G_ETL.validation;

import java.util.ArrayList;
import java.util.List;

public class ValidationResult {

    private boolean valid = true;
    private final List<String> errors = new ArrayList<>();

    public boolean isValid() {
        return valid;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void addError(String error) {
        valid = false;
        errors.add(error);
    }

    public void merge(ValidationResult other) {
        if (!other.isValid()) {
            this.valid = false;
            this.errors.addAll(other.getErrors());
        }
    }
}
