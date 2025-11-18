package org.jboss.resteasy.core.request;

import java.util.List;
import java.util.Locale;

import jakarta.ws.rs.core.Variant;

import org.junit.jupiter.api.*;

class ServerDrivenNegotiationTest {
    private ServerDrivenNegotiation unitUnderTest;

    @BeforeEach
    void setUp() {
        unitUnderTest = new ServerDrivenNegotiation();
    }

    @Nested
    class LanguageNegotiation {
        private final Variant de = new Variant(null, "de", null);
        private final Variant de_DE = new Variant(null, "de", "DE", null);
        private final Variant de_CH = new Variant(null, "de", "CH", null);
        private final Variant en = new Variant(null, "en", null);
        private final Variant en_US = new Variant(null, "en", "US", null);

        @Test
        @Disabled
        void getBestMatchWithGenericFirst() {
            // given
            List<Variant> availableGenericFirst = List.of(en, en_US, de, de_DE, de_CH);
            unitUnderTest.setAcceptLanguageHeaders(List.of("de-DE"));

            // when
            Variant result = unitUnderTest.getBestMatch(availableGenericFirst);

            // then
            Assertions.assertNotNull(result);
            Assertions.assertEquals(Locale.GERMANY, result.getLanguage());
        }

        @Test
        void getBestMatchWithSpecificFirst() {
            // given
            List<Variant> availableGenericFirst = List.of(en_US, en, de_DE, de_CH, de);
            unitUnderTest.setAcceptLanguageHeaders(List.of("de-DE"));

            // when
            Variant result = unitUnderTest.getBestMatch(availableGenericFirst);

            // then
            Assertions.assertNotNull(result);
            Assertions.assertEquals(Locale.GERMANY, result.getLanguage());
        }
    }
}
