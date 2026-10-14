package com.riwi.eventifyt.validation;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NoPastEventsValidatorTest {

    private final NoPastEventsValidator validator = new NoPastEventsValidator();

    @Test
    void yesterday_IsInvalid() {
        assertFalse(validator.isValid(LocalDate.now().minusDays(1), null));
    }

    @Test
    void today_IsValid() {
        assertTrue(validator.isValid(LocalDate.now(), null));
    }

    @Test
    void future_IsValid() {
        assertTrue(validator.isValid(LocalDate.now().plusDays(1), null));
    }

    @Test
    void null_IsValid_BecauseNotNullTakesCareOfIt() {
        assertTrue(validator.isValid(null, null));
    }
}
