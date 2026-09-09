package com.abhishek.expense.util;

import com.abhishek.expense.error.ApiException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import org.springframework.http.HttpStatus;

public final class DateTimes {

    private static final DateTimeFormatter RESPONSE_FORMAT = DateTimeFormatter
        .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    private DateTimes() {
    }

    public static LocalDateTime parseExpenseDate(String value) {
        if (value == null || value.isBlank()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "expenseDate must be a valid date");
        }

        String trimmed = value.trim();
        try {
            return LocalDate.parse(trimmed).atStartOfDay();
        } catch (DateTimeParseException ignored) {
            // Continue with timestamp formats.
        }

        try {
            return LocalDateTime.ofInstant(Instant.parse(trimmed), ZoneOffset.UTC);
        } catch (DateTimeParseException ignored) {
            // Continue with offset and local timestamps.
        }

        try {
            return OffsetDateTime.parse(trimmed).withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime();
        } catch (DateTimeParseException ignored) {
            // Continue with local timestamp.
        }

        try {
            return LocalDateTime.parse(trimmed);
        } catch (DateTimeParseException exception) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "expenseDate must be a valid date");
        }
    }

    public static String toApiTimestamp(LocalDateTime value) {
        return value == null ? null : RESPONSE_FORMAT.format(value);
    }
}
