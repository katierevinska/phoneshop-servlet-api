package com.es.phoneshop.utils;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class FieldValidationTest {

    @Test
    void testValidateNameValid() {
        String validName = "John Doe";
        Optional<String> result = FieldValidation.validateName(validName);
        assertFalse(result.isPresent());
    }

    @Test
    void testValidateNameInvalid() {
        String invalidName = "John123";
        Optional<String> result = FieldValidation.validateName(invalidName);
        assertTrue(result.isPresent());
        assertEquals("incorrect value", result.get());
    }

    @Test
    void testValidateAddressValid() {
        String validAddress = "123 Main St, Apt 4B";
        Optional<String> result = FieldValidation.validateAddress(validAddress);
        assertFalse(result.isPresent());
    }

    @Test
    void testValidateAddressInvalid() {
        String invalidAddress = "Main St @ 123";
        Optional<String> result = FieldValidation.validateAddress(invalidAddress);
        assertTrue(result.isPresent());
        assertEquals("incorrect value", result.get());
    }

    @Test
    void testValidatePhoneValid() {
        String validPhone = "+1234567890";
        Optional<String> result = FieldValidation.validatePhone(validPhone);
        assertFalse(result.isPresent());
    }

    @Test
    void testValidatePhoneInvalid() {
        String invalidPhone = "1234567890";
        Optional<String> result = FieldValidation.validatePhone(invalidPhone);
        assertTrue(result.isPresent());
        assertEquals("incorrect value", result.get());
    }

    @Test
    void testValidateNotEmptyStringValid() {
        String validString = "Some value";
        Optional<String> result = FieldValidation.validateNotEmptyString(validString);
        assertFalse(result.isPresent());
    }

    @Test
    void testValidateNotEmptyStringInvalid() {
        String emptyString = "";
        Optional<String> result = FieldValidation.validateNotEmptyString(emptyString);
        assertTrue(result.isPresent());
        assertEquals("value can't be empty", result.get());
    }

    @Test
    void testValidateDateInFutureValid() {
        String futureDate = LocalDate.now().plusDays(1).toString();
        Optional<String> result = FieldValidation.validateDateInFuture(futureDate);
        assertFalse(result.isPresent());
    }

    @Test
    void testValidateDateInFutureInvalid() {
        String pastDate = LocalDate.now().minusDays(1).toString();
        Optional<String> result = FieldValidation.validateDateInFuture(pastDate);
        assertTrue(result.isPresent());
        assertEquals("delivery date in the past", result.get());
    }

    @Test
    void testValidateDateInFutureEmpty() {
        String emptyDate = "";
        Optional<String> result = FieldValidation.validateDateInFuture(emptyDate);
        assertTrue(result.isPresent());
        assertEquals("value can't be empty", result.get());
    }
}