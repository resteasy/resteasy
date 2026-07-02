package org.jboss.resteasy.core.request;

import java.util.List;
import java.util.Locale;

import jakarta.ws.rs.core.Variant;

import org.junit.jupiter.api.*;

@DisplayName("Negotiate")
class ServerDrivenNegotiationTest {
    private ServerDrivenNegotiation unitUnderTest;

    @BeforeEach
    void setUp() {
        unitUnderTest = new ServerDrivenNegotiation();
    }

    @Nested
    @DisplayName("Language")
    class LanguageNegotiation {
        private final Variant de = new Variant(null, "de", null);
        private final Variant deDE = new Variant(null, "de", "DE", null);
        private final Variant deCH = new Variant(null, "de", "CH", null);
        private final Variant en = new Variant(null, "en", null);
        private final Variant enUS = new Variant(null, "en", "US", null);

        @Nested
        class GenericsFirst {
            private final List<Variant> availableLanguages = List.of(en, enUS, de, deDE, deCH);

            @Test
            @Disabled("Currently failing, see #4739")
            void acceptDeDE() {
                // given
                unitUnderTest.setAcceptLanguageHeaders(List.of("de-DE"));

                // when
                Variant result = unitUnderTest.getBestMatch(availableLanguages);

                // then
                Assertions.assertNotNull(result);
                Assertions.assertEquals(Locale.GERMANY, result.getLanguage());
            }

            @Test
            void acceptUnavailableSpecificLanguage() {
                // given
                unitUnderTest.setAcceptLanguageHeaders(List.of("de-AT"));

                // when
                Variant result = unitUnderTest.getBestMatch(availableLanguages);

                // then
                Assertions.assertNotNull(result);
                Assertions.assertEquals(Locale.GERMAN, result.getLanguage());
            }

            @Test
            void acceptGenericLanguage() {
                // given
                unitUnderTest.setAcceptLanguageHeaders(List.of("de"));

                // when
                Variant result = unitUnderTest.getBestMatch(availableLanguages);

                // then
                Assertions.assertNotNull(result);
                Assertions.assertEquals(Locale.GERMAN, result.getLanguage());
            }
        }

        @Nested
        @DisplayName("where specific languages are listed first.")
        class SpecificsFirst {
            private final List<Variant> availableLanguages = List.of(enUS, en, deDE, deCH, de);

            @Test
            void acceptAvailableSpecificLanguage() {
                // given
                unitUnderTest.setAcceptLanguageHeaders(List.of("de-DE"));

                // when
                Variant result = unitUnderTest.getBestMatch(availableLanguages);

                // then
                Assertions.assertNotNull(result);
                Assertions.assertEquals(Locale.GERMANY, result.getLanguage());
            }

            @Test
            void acceptUnavailableSpecificLanguage() {
                // given
                unitUnderTest.setAcceptLanguageHeaders(List.of("de-AT"));

                // when
                Variant result = unitUnderTest.getBestMatch(availableLanguages);

                // then
                Assertions.assertNotNull(result);
                Assertions.assertEquals(Locale.GERMAN, result.getLanguage());
            }

            @Test
            @Disabled("Currently failing, see #4739")
            void acceptGenericLanguage() {
                // given
                unitUnderTest.setAcceptLanguageHeaders(List.of("de"));

                // when
                Variant result = unitUnderTest.getBestMatch(availableLanguages);

                // then
                Assertions.assertNotNull(result);
                Assertions.assertEquals(Locale.GERMAN, result.getLanguage());
            }
        }
    }
}
