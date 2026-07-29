/*
 * Copyright The RESTEasy Authors
 * SPDX-License-Identifier: Apache-2.0
 */

package org.jboss.resteasy.core.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Variant;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link ServerDrivenNegotiation} language negotiation.
 *
 * @author <a href="mailto:jperkins@ibm.com">James R. Perkins</a>
 * @see <a href="https://www.rfc-editor.org/rfc/rfc4647">RFC 4647 — Matching of Language Tags</a>
 */
class ServerDrivenNegotiationTest {

    /**
     * Verifies that the most specific variant (matching all dimensions: media type, charset, encoding, language) is
     * preferred.
     */
    @Test
    void mostSpecific() {
        final ServerDrivenNegotiation negotiation = new ServerDrivenNegotiation();
        negotiation.setAcceptHeaders(List.of("text/plain"));
        negotiation.setAcceptCharsetHeaders(List.of(StandardCharsets.UTF_8.name()));
        negotiation.setAcceptEncodingHeaders(List.of("gzip"));
        negotiation.setAcceptLanguageHeaders(List.of("en-gb"));

        final MediaType mediaTypeWithCharset = MediaType.valueOf("text/plain; charset=UTF-8");
        final MediaType mediaType = MediaType.valueOf("text/plain");
        final String encoding = "gzip";
        final Locale locale = Locale.UK;

        final List<Variant> available = List.of(
                new Variant(mediaTypeWithCharset, (String) null, null),
                new Variant(mediaTypeWithCharset, locale, null),
                new Variant(mediaTypeWithCharset, (String) null, encoding),
                new Variant(mediaTypeWithCharset, locale, encoding),
                new Variant(mediaType, (String) null, null),
                new Variant(mediaType, locale, null),
                new Variant(mediaType, (String) null, encoding),
                new Variant(mediaType, locale, encoding),
                new Variant(null, locale, null),
                new Variant(null, locale, encoding),
                new Variant(null, (String) null, encoding));

        // Assert all acceptable:
        for (Variant variant : available) {
            assertEquals(variant, negotiation.getBestMatch(Collections.singletonList(variant)));
        }

        final Variant best = negotiation.getBestMatch(available);
        assertNotNull(best, "Variant should not be null");
        assertEquals(mediaTypeWithCharset, best.getMediaType(), "Wrong media type");
        assertEquals(encoding, best.getEncoding(), "Wrong encoding");
        assertEquals(locale, best.getLanguage(), "Wrong locale");
    }

    /**
     * Verifies the media type specificity tiebreaker: {@code text/html;level=1} > {@code text/html} >
     * {@code text/*} > {@code *}{@code /*}.
     */
    @Test
    void mostSpecificMediaType() {
        final String header = "text/*, text/html, text/html;level=1, */*";
        final ServerDrivenNegotiation negotiation = new ServerDrivenNegotiation();
        negotiation.setAcceptHeaders(List.of(header));

        final Variant htmlLevel1 = new Variant(MediaType.valueOf("text/html;level=1"), (String) null, null);
        final Variant html = new Variant(MediaType.valueOf("text/html"), (String) null, null);
        final Variant textWildcard = new Variant(MediaType.valueOf("text/*"), (String) null, null);
        final Variant wildcard = new Variant(MediaType.valueOf("*/*"), (String) null, null);

        // Add variants least-specific-first so each addition must displace the current winner, verifying each rung of
        // the specificity tiebreaker: type -> subtype -> parameters.
        final List<Variant> available = new ArrayList<>();
        available.add(wildcard);
        assertEquals(wildcard, negotiation.getBestMatch(available));
        available.add(textWildcard);
        assertEquals(textWildcard, negotiation.getBestMatch(available));
        available.add(html);
        assertEquals(html, negotiation.getBestMatch(available));
        available.add(htmlLevel1);
        assertEquals(htmlLevel1, negotiation.getBestMatch(available));
    }

    /**
     * Verifies quality factor ordering for media types across multiple Accept header values.
     */
    @Test
    void mediaTypeQualityFactor() {

        final String header1 = "text/*;q=0.3, text/html;q=0.7, text/html;level=1";
        final String header2 = "text/html;level=2;q=0.4, */*;q=0.5";
        final ServerDrivenNegotiation negotiation = new ServerDrivenNegotiation();
        negotiation.setAcceptHeaders(List.of(header1, header2));
        negotiation.setAcceptLanguageHeaders(List.of("en"));

        final Variant q03 = new Variant(MediaType.valueOf("text/plain"), (String) null, null);
        final Variant q04 = new Variant(MediaType.valueOf("text/html;level=2"), (String) null, null);
        final Variant q05 = new Variant(MediaType.valueOf("image/jpeg"), (String) null, null);
        final Variant q07 = new Variant(MediaType.valueOf("text/html"), (String) null, null);
        final Variant q07plus = new Variant(MediaType.valueOf("text/html;level=3"), (String) null, null);
        final Variant q10 = new Variant(MediaType.valueOf("text/html;level=1"), (String) null, null);

        // Add variants in ascending quality order so each addition must displace the current winner by having a higher
        // quality value.
        final List<Variant> available = new ArrayList<>();
        available.add(q03);
        assertEquals(q03, negotiation.getBestMatch(available));
        available.add(q04);
        assertEquals(q04, negotiation.getBestMatch(available));
        available.add(q05);
        assertEquals(q05, negotiation.getBestMatch(available));
        available.add(q07);
        assertEquals(q07, negotiation.getBestMatch(available));
        available.add(q07plus);
        assertEquals(q07plus, negotiation.getBestMatch(available));
        available.add(q10);
        assertEquals(q10, negotiation.getBestMatch(available));
    }

    /**
     * RFC 4647 Section 3.3.1: an exact country match (longest range) is preferred over a base-language match.
     */
    @Test
    void languageExactRegionPreferred() {
        final List<Variant> available = List.of(
                new Variant(null, "de", "CH", null),
                new Variant(null, "de", "DE", null),
                new Variant(null, "de", "", null));

        final ServerDrivenNegotiation negotiation = new ServerDrivenNegotiation();
        negotiation.setAcceptLanguageHeaders(List.of("de-DE, de"));
        assertEquals(Locale.forLanguageTag("de-DE"), negotiation.getBestMatch(available).getLanguage(),
                "Explicitly requested de-DE should be preferred over de-CH");
    }

    /**
     * Practical extension: range {@code de-LU} "reverse matches" tag {@code de} at BASE_LANGUAGE precision when no
     * exact match exists.
     */
    @Test
    void languageFallbackToBase() {
        final List<Variant> available = List.of(
                new Variant(null, "de", "CH", null),
                new Variant(null, "de", "DE", null),
                new Variant(null, "de", "", null));

        final ServerDrivenNegotiation negotiation = new ServerDrivenNegotiation();
        negotiation.setAcceptLanguageHeaders(List.of("de-LU"));
        assertEquals(Locale.forLanguageTag("de"), negotiation.getBestMatch(available).getLanguage(),
                "de-LU should fall back to de when no de-LU variant is available");
    }

    /**
     * RFC 4647 Section 3.3.1: exact match when range and tag are identical ({@code de-CH}).
     */
    @Test
    void languageExactRegionMatch() {
        final List<Variant> available = List.of(
                new Variant(null, "de", "CH", null),
                new Variant(null, "de", "DE", null),
                new Variant(null, "de", "", null));

        final ServerDrivenNegotiation negotiation = new ServerDrivenNegotiation();
        negotiation.setAcceptLanguageHeaders(List.of("de-CH"));
        assertEquals(Locale.forLanguageTag("de-CH"), negotiation.getBestMatch(available).getLanguage(),
                "Exact de-CH request should match de-CH variant");
    }

    /**
     * RFC 7231 Section 5.3.5: higher quality factor always wins regardless of match precision.
     */
    @Test
    void languageQualityValueRespected() {
        final List<Variant> available = List.of(
                new Variant(null, "en", "US", null),
                new Variant(null, "en", "", null),
                new Variant(null, "en", "GB", null));

        final ServerDrivenNegotiation negotiation = new ServerDrivenNegotiation();
        negotiation.setAcceptLanguageHeaders(List.of("en-GB;q=0.7, en"));
        assertEquals(Locale.forLanguageTag("en"), negotiation.getBestMatch(available).getLanguage(),
                "Higher quality en (q=1.0) should beat en-GB (q=0.7)");
    }

    /**
     * RFC 7231 Section 5.3.5: quality factor dominates -- {@code en-US} (q=1.0) beats {@code en-GB} (q=0.9).
     */
    @Test
    void languageQualityPriority() {
        final List<Variant> available = List.of(
                new Variant(null, "en", "US", null),
                new Variant(null, "en", "", null),
                new Variant(null, "en", "GB", null));

        final ServerDrivenNegotiation negotiation = new ServerDrivenNegotiation();
        negotiation.setAcceptLanguageHeaders(List.of("en-GB;q=0.9, en-US"));
        assertEquals(Locale.forLanguageTag("en-US"), negotiation.getBestMatch(available).getLanguage(),
                "en-US (q=1.0) should beat en-GB (q=0.9)");
    }

    /**
     * When quality is equal, the variant whose matching range appears first in Accept-Language wins.
     */
    @Test
    void languageHeaderOrderPriority() {
        final List<Variant> available = List.of(
                new Variant(null, "en", "US", null),
                new Variant(null, "en", "", null),
                new Variant(null, "en", "GB", null));

        final ServerDrivenNegotiation negotiation = new ServerDrivenNegotiation();
        negotiation.setAcceptLanguageHeaders(List.of("en-GB, en-US"));
        assertEquals(Locale.forLanguageTag("en-GB"), negotiation.getBestMatch(available).getLanguage(),
                "en-GB should win as first in Accept-Language header with equal quality");
    }

    /**
     * Explicitly requested {@code en-GB} is preferred over generic {@code en}.
     */
    @Test
    void languageSpecificOverGeneric() {
        final List<Variant> available = List.of(
                new Variant(null, "en", "US", null),
                new Variant(null, "en", "", null),
                new Variant(null, "en", "GB", null));

        final ServerDrivenNegotiation negotiation = new ServerDrivenNegotiation();
        negotiation.setAcceptLanguageHeaders(List.of("en-GB, en"));
        assertEquals(Locale.forLanguageTag("en-GB"), negotiation.getBestMatch(available).getLanguage(),
                "Explicitly requested en-GB should be preferred over generic en");
    }

    /**
     * Verifies that {@link ServerDrivenNegotiation} instances are reusable -- fresh {@link VariantQuality} per call
     * prevents stale state.
     */
    @Test
    void languageNegotiationInstanceReuse() {
        final ServerDrivenNegotiation negotiation = new ServerDrivenNegotiation();
        negotiation.setAcceptLanguageHeaders(List.of("de-DE, de"));

        final List<Variant> available1 = List.of(
                new Variant(null, "de", "CH", null),
                new Variant(null, "de", "DE", null),
                new Variant(null, "de", "", null));
        assertEquals(Locale.forLanguageTag("de-DE"), negotiation.getBestMatch(available1).getLanguage(),
                "First call should return de-DE");

        final List<Variant> available2 = List.of(
                new Variant(null, "de", "CH", null),
                new Variant(null, "de", "", null));
        assertEquals(Locale.forLanguageTag("de"), negotiation.getBestMatch(available2).getLanguage(),
                "Second call should still work correctly and return de");

        assertEquals(Locale.forLanguageTag("de-DE"), negotiation.getBestMatch(available1).getLanguage(),
                "Third call should still return de-DE");
    }

    /**
     * RFC 4647 Section 3.3.1: no match when base languages differ entirely.
     */
    @Test
    void languageNoMatch() {
        final List<Variant> available = List.of(
                new Variant(null, "de", "DE", null),
                new Variant(null, "de", "", null));

        final ServerDrivenNegotiation negotiation = new ServerDrivenNegotiation();
        negotiation.setAcceptLanguageHeaders(List.of("fr"));
        assertNull(negotiation.getBestMatch(available),
                "No match for fr when only de variants are available");
    }

    /**
     * RFC 4647 Section 3.3.1: wildcard ({@code *}) matches any language tag not matched by another range.
     */
    @Test
    void languageWildcard() {
        final List<Variant> available = List.of(
                new Variant(null, "ja", "", null),
                new Variant(null, "de", "", null));

        final ServerDrivenNegotiation negotiation = new ServerDrivenNegotiation();
        negotiation.setAcceptLanguageHeaders(List.of("*"));
        assertNotNull(negotiation.getBestMatch(available),
                "Wildcard should match any available language");
    }

    /**
     * RFC 7231 Section 5.3.1: quality value of 0 means "not acceptable" -- the language must be excluded.
     */
    @Test
    void languageQualityZeroExclusion() {
        final List<Variant> available = List.of(
                new Variant(null, "en", "", null),
                new Variant(null, "fr", "", null));

        // Partial exclusion: one language excluded, other selected
        ServerDrivenNegotiation negotiation = new ServerDrivenNegotiation();
        negotiation.setAcceptLanguageHeaders(List.of("en;q=0, fr"));
        assertEquals(Locale.forLanguageTag("fr"), negotiation.getBestMatch(available).getLanguage(),
                "en with q=0 should be excluded, fr should be selected");

        // Full exclusion: all languages excluded returns null
        negotiation = new ServerDrivenNegotiation();
        negotiation.setAcceptLanguageHeaders(List.of("en;q=0, fr;q=0"));
        assertNull(negotiation.getBestMatch(available),
                "All languages excluded with q=0 should return null");
    }

    /**
     * Verifies that Accept media type filtering takes precedence over Accept-Language position tiebreaking.
     */
    @Test
    void languageWithMediaType() {
        final List<Variant> available = List.of(
                new Variant(MediaType.APPLICATION_JSON_TYPE, "en", "GB", null),
                new Variant(MediaType.APPLICATION_XML_TYPE, "en", "US", null));

        final ServerDrivenNegotiation negotiation = new ServerDrivenNegotiation();
        negotiation.setAcceptHeaders(List.of("application/xml"));
        negotiation.setAcceptLanguageHeaders(List.of("en-GB, en-US"));
        final Variant best = negotiation.getBestMatch(available);
        assertEquals(MediaType.APPLICATION_XML_TYPE, best.getMediaType(),
                "Media type quality should take precedence over language header order");
        assertEquals(Locale.forLanguageTag("en-US"), best.getLanguage());
    }

    /**
     * Practical extension: reverse-prefix match ({@code en-US} matching tag {@code en}) retains the position of the
     * matching range for tiebreaking.
     */
    @Test
    void languageFuzzyMatchPreservesPositionPriority() {
        final List<Variant> available = List.of(
                new Variant(null, "en", "", null),
                new Variant(null, "de", "", null));

        final ServerDrivenNegotiation negotiation = new ServerDrivenNegotiation();
        negotiation.setAcceptLanguageHeaders(List.of("en-US, de"));
        assertEquals(Locale.forLanguageTag("en"), negotiation.getBestMatch(available).getLanguage(),
                "en should win — en-US is listed before de in Accept-Language");
    }

    /**
     * Family position inheritance: variant {@code en-GB} keeps the position from base match {@code en} (pos 1), not
     * from country-mismatched {@code en-US} (pos 3).
     */
    @Test
    void languagePositionNotOverwrittenByCountryMismatch() {
        final List<Variant> available = List.of(
                new Variant(null, "en", "GB", null),
                new Variant(null, "fr", "FR", null));

        final ServerDrivenNegotiation negotiation = new ServerDrivenNegotiation();
        negotiation.setAcceptLanguageHeaders(List.of("en, fr, en-US"));
        assertEquals(Locale.forLanguageTag("en-GB"), negotiation.getBestMatch(available).getLanguage(),
                "en-GB should win — its match came from 'en' at position 1, not 'en-US' at position 3");
    }

    /**
     * BASE_LANGUAGE match stability: a later, lower-quality base match must not overwrite an earlier, higher-quality
     * one.
     */
    @Test
    void languageBaseMatchNotOverwrittenByLaterBaseMatch() {
        final List<Variant> available = List.of(
                new Variant(null, "en", "", null),
                new Variant(null, "de", "", null));

        // Equal quality: later BASE_LANGUAGE match must not overwrite position
        ServerDrivenNegotiation negotiation = new ServerDrivenNegotiation();
        negotiation.setAcceptLanguageHeaders(List.of("en-US, de, en-AU"));
        assertEquals(Locale.forLanguageTag("en"), negotiation.getBestMatch(available).getLanguage(),
                "en should win — en-US is listed before de; later en-AU must not overwrite the position");

        // Different quality: later BASE_LANGUAGE match must not overwrite higher quality
        negotiation = new ServerDrivenNegotiation();
        negotiation.setAcceptLanguageHeaders(List.of("en-US;q=0.9, de;q=0.5, en-AU;q=0.3"));
        assertEquals(Locale.forLanguageTag("en"), negotiation.getBestMatch(available).getLanguage(),
                "en should win — en-US (q=0.9) quality must not be overwritten by en-AU (q=0.3)");
    }

    /**
     * Variants without a language dimension pass through language negotiation unconditionally.
     */
    @Test
    void languageVariantWithoutLanguage() {
        final List<Variant> available = List.of(
                new Variant(MediaType.APPLICATION_JSON_TYPE, (String) null, null),
                new Variant(null, "de", "DE", null));

        final ServerDrivenNegotiation negotiation = new ServerDrivenNegotiation();
        negotiation.setAcceptHeaders(List.of("application/json"));
        negotiation.setAcceptLanguageHeaders(List.of("fr"));
        final Variant best = negotiation.getBestMatch(available);
        assertNotNull(best, "Variant without language should still be selectable");
        assertEquals(MediaType.APPLICATION_JSON_TYPE, best.getMediaType(),
                "Language-less variant should pass through when no language variant matches");
    }

    /**
     * RFC 7231 Section 5.3.5 example: {@code da, en-gb;q=0.8, en;q=0.7} -- verifies quality assignment and ranking.
     */
    @Test
    void rfc7231QualityRanking() {
        // RFC 7231 Section 5.3.5 example: "Accept-Language: da, en-gb;q=0.8, en;q=0.7"
        // Expected quality assignment:
        //   da     -> q=1.0 (exact match, implicit q=1.0)
        //   en-gb  -> q=0.8 (exact match)
        //   en     -> q=0.7 (exact match)
        //   en-us  -> q=0.7 (matched via range 'en')
        final ServerDrivenNegotiation negotiation = new ServerDrivenNegotiation();
        negotiation.setAcceptLanguageHeaders(List.of("da, en-gb;q=0.8, en;q=0.7"));

        final Variant da = new Variant(null, "da", "", null);
        final Variant enGb = new Variant(null, "en", "GB", null);
        final Variant en = new Variant(null, "en", "", null);
        final Variant enUs = new Variant(null, "en", "US", null);

        // da (q=1.0) is the top choice
        assertEquals(da.getLanguage(), negotiation.getBestMatch(List.of(da, enGb, en, enUs)).getLanguage(),
                "da (q=1.0) should win over all English variants");

        // Without da, en-gb (q=0.8) beats en/en-us (q=0.7)
        assertEquals(enGb.getLanguage(), negotiation.getBestMatch(List.of(enGb, en, enUs)).getLanguage(),
                "en-gb (q=0.8) should beat en and en-us (q=0.7)");

        // Without da or en-gb, en beats en-us (both q=0.7 but en matches at an earlier position)
        assertEquals(en.getLanguage(), negotiation.getBestMatch(List.of(en, enUs)).getLanguage(),
                "en should beat en-us when both have q=0.7");

        // Same result regardless of variant list order
        assertEquals(en.getLanguage(), negotiation.getBestMatch(List.of(enUs, en)).getLanguage(),
                "en should still beat en-us when variant list is reversed");
    }

    /**
     * RFC 7231 Section 5.3.5: quality comes from the longest (most specific) matching range, not a shorter prefix.
     */
    @Test
    void longestMatchDeterminesQuality() {
        // RFC 4647: quality comes from the most specific (longest) matching range.
        // en-gb should get q=0.9 from its exact match, not q=0.7 from the base 'en'.
        // en should get q=0.7 from its exact match, not q=0.9 from en-gb fallback.
        final Variant enGb = new Variant(null, "en", "GB", null);
        final Variant en = new Variant(null, "en", "", null);
        final Variant de = new Variant(null, "de", "", null);

        ServerDrivenNegotiation negotiation = new ServerDrivenNegotiation();
        negotiation.setAcceptLanguageHeaders(List.of("en;q=0.7, en-gb;q=0.9"));

        assertEquals(enGb.getLanguage(), negotiation.getBestMatch(List.of(enGb, en)).getLanguage(),
                "en-gb should use its exact match quality (0.9), beating en (0.7)");

        // Verify en truly gets 0.7, not 0.9: pit it against de at 0.75
        negotiation = new ServerDrivenNegotiation();
        negotiation.setAcceptLanguageHeaders(List.of("en;q=0.7, en-gb;q=0.9, de;q=0.75"));
        assertEquals(de.getLanguage(), negotiation.getBestMatch(List.of(en, de)).getLanguage(),
                "en should get q=0.7 (its exact match), losing to de (q=0.75)");

        // Same test with Accept-Language order reversed
        negotiation = new ServerDrivenNegotiation();
        negotiation.setAcceptLanguageHeaders(List.of("en-gb;q=0.9, de;q=0.75, en;q=0.7"));
        assertEquals(de.getLanguage(), negotiation.getBestMatch(List.of(en, de)).getLanguage(),
                "Result should not depend on Accept-Language entry order when qualities differ");
    }

    /**
     * Verifies that the selected variant does not depend on the order of the available variants list.
     */
    @Test
    void variantListOrderIndependence() {
        // The selected variant must not change when the available list is reordered.
        final ServerDrivenNegotiation negotiation = new ServerDrivenNegotiation();
        negotiation.setAcceptLanguageHeaders(List.of("de-DE, en;q=0.8, fr;q=0.5"));

        final Variant deDE = new Variant(null, "de", "DE", null);
        final Variant en = new Variant(null, "en", "", null);
        final Variant fr = new Variant(null, "fr", "", null);
        final Variant de = new Variant(null, "de", "", null);

        final List<Variant> canonical = List.of(deDE, en, fr, de);
        final Locale expected = negotiation.getBestMatch(canonical).getLanguage();

        // Test multiple permutations — winner must always be the same
        final List<List<Variant>> permutations = List.of(
                List.of(de, fr, en, deDE),
                List.of(fr, deDE, de, en),
                List.of(en, de, deDE, fr),
                List.of(fr, en, de, deDE),
                List.of(deDE, de, fr, en),
                List.of(de, deDE, en, fr));
        for (List<Variant> perm : permutations) {
            assertEquals(expected, negotiation.getBestMatch(perm).getLanguage(),
                    "Winner should be " + expected + " regardless of variant order, but failed for: " + perm);
        }
    }

    /**
     * Quality factor always dominates position tiebreaking -- higher q wins regardless of header position.
     */
    @Test
    void qualityAlwaysDominatesPosition() {
        // A higher quality range must always win, regardless of position in the header.
        final Variant en = new Variant(null, "en", "", null);
        final Variant de = new Variant(null, "de", "", null);
        final List<Variant> available = List.of(en, de);

        // en listed second but with higher quality
        ServerDrivenNegotiation negotiation = new ServerDrivenNegotiation();
        negotiation.setAcceptLanguageHeaders(List.of("de;q=0.5, en;q=0.9"));
        assertEquals(en.getLanguage(), negotiation.getBestMatch(available).getLanguage(),
                "en (q=0.9) should beat de (q=0.5) despite de being listed first");

        // de listed second but with higher quality
        negotiation = new ServerDrivenNegotiation();
        negotiation.setAcceptLanguageHeaders(List.of("en;q=0.5, de;q=0.9"));
        assertEquals(de.getLanguage(), negotiation.getBestMatch(available).getLanguage(),
                "de (q=0.9) should beat en (q=0.5) despite en being listed first");

        // Three-way: middle entry has highest quality
        final Variant fr = new Variant(null, "fr", "", null);
        negotiation = new ServerDrivenNegotiation();
        negotiation.setAcceptLanguageHeaders(List.of("en;q=0.5, de;q=0.9, fr;q=0.7"));
        assertEquals(de.getLanguage(), negotiation.getBestMatch(List.of(en, de, fr)).getLanguage(),
                "de (q=0.9) should beat en (q=0.5) and fr (q=0.7)");
    }

    /**
     * When all qualities are equal, the variant whose matching range appears first in Accept-Language wins, regardless
     * of variant list order.
     */
    @Test
    void positionTiebreakAllPermutations() {
        // When all variants have equal quality, the winner is determined solely by which variant's matching range
        // appears first in Accept-Language. This must hold for every permutation of the variant list.
        final Variant en = new Variant(null, "en", "", null);
        final Variant de = new Variant(null, "de", "", null);
        final Variant fr = new Variant(null, "fr", "", null);

        final ServerDrivenNegotiation negotiation = new ServerDrivenNegotiation();
        negotiation.setAcceptLanguageHeaders(List.of("en, de, fr"));

        // All 6 permutations of [en, de, fr] — en should always win (position 1)
        final List<List<Variant>> permutations = List.of(
                List.of(en, de, fr),
                List.of(en, fr, de),
                List.of(de, en, fr),
                List.of(de, fr, en),
                List.of(fr, en, de),
                List.of(fr, de, en));
        for (List<Variant> perm : permutations) {
            assertEquals(en.getLanguage(), negotiation.getBestMatch(perm).getLanguage(),
                    "en (position 1) should always win with equal quality, variant order: " + perm);
        }

        // Remove en — de (position 2) should always win
        for (List<Variant> perm : List.of(List.of(de, fr), List.of(fr, de))) {
            assertEquals(de.getLanguage(), negotiation.getBestMatch(perm).getLanguage(),
                    "Without en, de (position 2) should beat fr (position 3)");
        }
    }

    /**
     * Family position inheritance: variant {@code en-US} retains the family position from {@code en} (pos 1) when
     * upgrading to EXACT_COUNTRY from {@code en-US} (pos 2) at the same quality. Mirrors
     * {@code VariantsTest.testGetComplexAcceptLanguageEnUs} integration test.
     */
    @Test
    void sameQualityFamilyPreservesFirstMatchPosition() {
        // Accept-Language: en, en-us (both q=1.0)
        // Variant en-us gets BASE_LANGUAGE from 'en' (pos=1, q=1.0) then EXACT_COUNTRY from
        // 'en-us' (pos=2, q=1.0). Since qualities match, the family position (1) is retained.
        // This ensures that a coherent family preference (en + en-us) doesn't penalize the more specific variant on
        // position. Mirrors VariantsTest.testGetComplexAcceptLanguageEnUs integration test.
        final ServerDrivenNegotiation negotiation = new ServerDrivenNegotiation();
        negotiation.setAcceptHeaders(List.of("text/xml, application/xml, application/xhtml+xml, image/png, "
                + "text/html;q=0.9, text/plain;q=0.8, */*;q=0.5"));
        negotiation.setAcceptLanguageHeaders(List.of("en, en-us"));

        final List<Variant> available = List.of(
                new Variant(MediaType.valueOf("image/jpeg"), (String) null, null),
                new Variant(MediaType.valueOf("application/xml"), "en", "US", null),
                new Variant(MediaType.valueOf("text/xml"), "en", "", null),
                new Variant(MediaType.valueOf("text/xml"), "en", "US", null));

        final Variant best = negotiation.getBestMatch(available);
        assertNotNull(best);
        assertEquals(MediaType.valueOf("application/xml"), best.getMediaType(),
                "application/xml + en-us should win — en-us keeps family position from 'en'");
        assertEquals(Locale.forLanguageTag("en-US"), best.getLanguage());
    }

    /**
     * RFC 7231 Section 5.3.5: the longest match determines quality. {@code en-GB} gets q=0.3 from its exact match,
     * not q=0.9 from the shorter {@code en} range.
     */
    @Test
    void exactCountryDowngradesQualityFromBaseLanguage() {
        // Accept-Language: en;q=0.9, en-GB;q=0.3, de-DE
        // en-GB first matches 'en' as BASE_LANGUAGE (q=0.9), then 'en-GB' as EXACT_COUNTRY (q=0.3).
        // The most specific match should win per RFC 4647, so en-GB gets q=0.3.
        // de-DE matches at q=1.0 and should be selected over en-GB.
        final ServerDrivenNegotiation negotiation = new ServerDrivenNegotiation();
        negotiation.setAcceptLanguageHeaders(List.of("en;q=0.9, en-GB;q=0.3, de-DE"));

        final List<Variant> available = List.of(
                new Variant(null, "en", "GB", null),
                new Variant(null, "de", "DE", null));

        assertEquals(Locale.forLanguageTag("de-DE"), negotiation.getBestMatch(available).getLanguage(),
                "de-DE (q=1.0) should beat en-GB (q=0.3) — exact country match overrides higher base-language quality");
    }

    /**
     * An explicitly matched language always beats a wildcard-matched one, even when the wildcard appears earlier in
     * Accept-Language.
     */
    @Test
    void explicitMatchBeatsWildcard() {
        final ServerDrivenNegotiation negotiation = new ServerDrivenNegotiation();
        negotiation.setAcceptLanguageHeaders(List.of("*, en"));

        final Variant fr = new Variant(null, "fr", "", null);
        final Variant en = new Variant(null, "en", "", null);
        assertEquals(en.getLanguage(), negotiation.getBestMatch(List.of(fr, en)).getLanguage(),
                "Explicitly matched en should beat wildcard-matched fr regardless of position");
        assertEquals(en.getLanguage(), negotiation.getBestMatch(List.of(en, fr)).getLanguage(),
                "Result should be the same regardless of variant list order");
    }

    /**
     * Wildcard fallback: when a BASE_LANGUAGE match yields q=0, the wildcard should be used instead of rejecting the
     * variant.
     */
    @Test
    void wildcardFallbackWhenBaseLanguageExcluded() {
        final ServerDrivenNegotiation negotiation = new ServerDrivenNegotiation();
        negotiation.setAcceptLanguageHeaders(List.of("en-US;q=0, *"));

        final Variant en = new Variant(null, "en", "", null);
        assertNotNull(negotiation.getBestMatch(List.of(en)),
                "en should match via wildcard fallback when base-language match has q=0");
    }

    /**
     * Position update on quality change: when an EXACT_COUNTRY match has different quality than a prior BASE_LANGUAGE
     * match, the position must update to the EXACT_COUNTRY range's position.
     */
    @Test
    void precisionUpgradeShouldUpdatePosition() {
        // Accept-Language: en;q=0.5, de-DE, en-US
        // Without the fix, en-US would keep the stale position 1 from the 'en' base match (q=0.5) even after upgrading
        // to the EXACT_COUNTRY match at position 3 (q=1.0). That stale position would let en-US (pos=1) beat de-DE
        // (pos=2) even though the user listed de-DE before en-US. The fix updates position when quality changes.
        final List<Variant> available = List.of(
                new Variant(null, "en", "US", null),
                new Variant(null, "de", "DE", null));

        final ServerDrivenNegotiation negotiation = new ServerDrivenNegotiation();
        negotiation.setAcceptLanguageHeaders(List.of("en;q=0.5, de-DE, en-US"));
        assertEquals(Locale.forLanguageTag("de-DE"), negotiation.getBestMatch(available).getLanguage(),
                "de-DE (position 2) should beat en-US (position 3) — the stale position from 'en' at position 1 should not give en-US an unfair advantage");
    }

    // --- New edge-case tests ---

    /**
     * RESTEASY-3675 reproduction case: {@code Accept-Language: de-DE} with variants {@code [en, en-US, de, de-DE]}
     * must return {@code de-DE}, not {@code de}. The result must not depend on variant list order.
     *
     * @see <a href="https://github.com/orgs/resteasy/discussions/4739">Discussion #4739</a>
     */
    @Test
    void exactCountryBeatsBaseRegardlessOfVariantOrder() {
        final ServerDrivenNegotiation negotiation = new ServerDrivenNegotiation();
        negotiation.setAcceptLanguageHeaders(List.of("de-DE"));

        final Variant en = new Variant(null, "en", "", null);
        final Variant enUS = new Variant(null, "en", "US", null);
        final Variant de = new Variant(null, "de", "", null);
        final Variant deDE = new Variant(null, "de", "DE", null);

        // Original variant order from the bug report: de appears before de-DE
        assertEquals(Locale.forLanguageTag("de-DE"),
                negotiation.getBestMatch(List.of(en, enUS, de, deDE)).getLanguage(),
                "de-DE should win over de even when de appears first in the variant list");

        // Reversed: de-DE appears before de
        assertEquals(Locale.forLanguageTag("de-DE"),
                negotiation.getBestMatch(List.of(deDE, de, enUS, en)).getLanguage(),
                "de-DE should still win when variant list is reversed");

        // Minimal reproduction: just de and de-DE
        assertEquals(Locale.forLanguageTag("de-DE"),
                negotiation.getBestMatch(List.of(de, deDE)).getLanguage(),
                "de-DE should win over de in minimal variant list");
        assertEquals(Locale.forLanguageTag("de-DE"),
                negotiation.getBestMatch(List.of(deDE, de)).getLanguage(),
                "de-DE should win regardless of which comes first");
    }

    /**
     * Multiple BASE_LANGUAGE matches: when several ranges match the same variant at BASE_LANGUAGE precision, the
     * highest quality should be used.
     */
    @Test
    void multipleBaseLanguageMatchesPicksHighestQuality() {
        final ServerDrivenNegotiation negotiation = new ServerDrivenNegotiation();
        negotiation.setAcceptLanguageHeaders(List.of("en-US;q=0.3, en-AU;q=0.9"));

        final Variant en = new Variant(null, "en", "", null);
        final Variant de = new Variant(null, "de", "", null);

        // en should get q=0.9 from the en-AU range (higher of the two base matches)
        // Verify by pitting against de at q=0.5: en (q=0.9) should win
        final ServerDrivenNegotiation negotiation2 = new ServerDrivenNegotiation();
        negotiation2.setAcceptLanguageHeaders(List.of("en-US;q=0.3, en-AU;q=0.9, de;q=0.5"));
        assertEquals(Locale.forLanguageTag("en"), negotiation2.getBestMatch(List.of(en, de)).getLanguage(),
                "en should get q=0.9 from the highest-quality base match (en-AU), beating de (q=0.5)");

        // Reverse: en-AU first, en-US second — same result expected
        final ServerDrivenNegotiation negotiation3 = new ServerDrivenNegotiation();
        negotiation3.setAcceptLanguageHeaders(List.of("en-AU;q=0.9, en-US;q=0.3, de;q=0.5"));
        assertEquals(Locale.forLanguageTag("en"), negotiation3.getBestMatch(List.of(en, de)).getLanguage(),
                "en should get q=0.9 regardless of range order");
    }

    /**
     * Range {@code en-US} with variant {@code en-US} should produce EXACT_COUNTRY, not BASE_LANGUAGE.
     */
    @Test
    void reverseMatchDoesNotOverrideExactCountry() {
        final ServerDrivenNegotiation negotiation = new ServerDrivenNegotiation();
        negotiation.setAcceptLanguageHeaders(List.of("en-US"));

        final Variant enUS = new Variant(null, "en", "US", null);
        final Variant en = new Variant(null, "en", "", null);

        // en-US should win over en because EXACT_COUNTRY > BASE_LANGUAGE in precision tiebreaker
        assertEquals(Locale.forLanguageTag("en-US"), negotiation.getBestMatch(List.of(en, enUS)).getLanguage(),
                "en-US should be EXACT_COUNTRY match, beating en (BASE_LANGUAGE)");
        assertEquals(Locale.forLanguageTag("en-US"), negotiation.getBestMatch(List.of(enUS, en)).getLanguage(),
                "Result should not depend on variant list order");
    }
}
