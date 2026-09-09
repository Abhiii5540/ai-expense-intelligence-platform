package com.abhishek.expense.util;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class EmailNormalizerTest {

    @Test
    void matchesLegacyGmailNormalization() {
        assertThat(EmailNormalizer.normalize(" Ab.Hi+portfolio@GoogleMail.com "))
            .isEqualTo("abhi@gmail.com");
    }

    @Test
    void matchesLegacyProviderSpecificSubaddresses() {
        assertThat(EmailNormalizer.normalize("Abhi+work@Outlook.com"))
            .isEqualTo("abhi@outlook.com");
        assertThat(EmailNormalizer.normalize("Abhi-shopping@yahoo.com"))
            .isEqualTo("abhi@yahoo.com");
        assertThat(EmailNormalizer.normalize("Abhi@ya.ru"))
            .isEqualTo("abhi@yandex.ru");
    }
}
