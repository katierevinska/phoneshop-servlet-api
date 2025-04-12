package com.es.phoneshop.utils;

import java.time.LocalDate;
import java.util.Optional;

public class FieldValidation {
    public static final String NAME_PATTERN = "^[A-Za-z]+(([' -][A-Za-z])?[A-Za-z]*)*$";
    public static final String ADDRESS_PATTERN = "^[A-Za-z0-9\\s.,-]+$";
    public static final String PHONE_PATTERN = "^\\+\\d[\\d\\s\\-\\(\\)]{9,20}$";

    public static Optional<String> validateName(String fieldValue) {
        return validateByPattern(fieldValue, NAME_PATTERN);
    }

    public static Optional<String> validateAddress(String fieldValue) {
        return validateByPattern(fieldValue, ADDRESS_PATTERN);
    }

    public static Optional<String> validatePhone(String fieldValue) {
        return validateByPattern(fieldValue, PHONE_PATTERN);
    }

    public static Optional<String> validateNotEmptyString(String fieldValue) {
        String errorMessage = null;
        if (fieldValue.isEmpty()) {
            errorMessage = "value can't be empty";
        }
        return Optional.ofNullable(errorMessage);
    }

    public static Optional<String> validateDateInFuture(String deliveryDateStr) {
        return validateNotEmptyString(deliveryDateStr)
                .or(() -> LocalDate.parse(deliveryDateStr).isBefore(LocalDate.now())
                        ? Optional.of("delivery date in the past")
                        : Optional.empty());
    }

    private static Optional<String> validateByPattern(String fieldValue, String pattern) {
        return validateNotEmptyString(fieldValue)
                .or(() -> fieldValue.matches(pattern)
                        ? Optional.empty()
                        : Optional.of("incorrect value"));
    }
}
