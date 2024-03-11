package org.jboss.resteasy.test.request;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Variant;

import org.jboss.resteasy.core.request.ServerDrivenNegotiation;
import org.junit.jupiter.api.Test;

/**
 * @tpSubChapter Requests
 * @tpChapter Unit tests
 * @tpTestCaseDetails Test for ServerDrivenNegotiation class.
 * @tpSince RESTEasy 3.0.16
 */
public class VariantSelectionTest {

    final String ERROR_MSG = "Wrong media type";

    @Test
    public void mostSpecific() {
        ServerDrivenNegotiation negotiation = new ServerDrivenNegotiation();
        negotiation.setAcceptHeaders(Arrays.asList("text/plain"));
        negotiation.setAcceptCharsetHeaders(Arrays.asList(StandardCharsets.UTF_8.name()));
        negotiation.setAcceptEncodingHeaders(Arrays.asList("gzip"));
        negotiation.setAcceptLanguageHeaders(Arrays.asList("en-gb"));

        MediaType mediaTypeWithCharset = MediaType.valueOf("text/plain; charset=UTF-8");
        MediaType mediaType = MediaType.valueOf("text/plain");
        String encoding = "gzip";
        Locale locale = Locale.UK;

        List<Variant> available = new ArrayList<Variant>();
        available.add(new Variant(mediaTypeWithCharset, (String) null, (String) null));
        available.add(new Variant(mediaTypeWithCharset, locale, null));
        available.add(new Variant(mediaTypeWithCharset, (String) null, encoding));
        available.add(new Variant(mediaTypeWithCharset, locale, encoding));
        available.add(new Variant(mediaType, (String) null, (String) null));
        available.add(new Variant(mediaType, locale, null));
        available.add(new Variant(mediaType, (String) null, encoding));
        available.add(new Variant(mediaType, locale, encoding));
        available.add(new Variant(null, locale, null));
        available.add(new Variant(null, locale, encoding));
        available.add(new Variant((MediaType) null, (String) null, encoding));

        // Assert all acceptable:
        for (Variant variant : available) {
            assertEquals(variant, negotiation.getBestMatch(Arrays.asList(variant)));
        }

        Variant best = negotiation.getBestMatch(available);
        assertNotNull(best, "Variant should not be null");
        assertEquals(mediaTypeWithCharset, best.getMediaType(), "Wrong media type");
        assertEquals(encoding, best.getEncoding(), "Wrong encoding");
        assertEquals(locale, best.getLanguage(), "Wrong locale");
    }

    @Test
    public void mostSpecificMediaType() {
        String header = "text/*, text/html, text/html;level=1, */*";
        ServerDrivenNegotiation negotiation = new ServerDrivenNegotiation();
        negotiation.setAcceptHeaders(Arrays.asList(header));

        Variant o1 = new Variant(MediaType.valueOf("text/html;level=1"), (String) null, null);
        Variant o2 = new Variant(MediaType.valueOf("text/html"), (String) null, null);
        Variant o3 = new Variant(MediaType.valueOf("text/*"), (String) null, null);
        Variant o4 = new Variant(MediaType.valueOf("*/*"), (String) null, null);

        List<Variant> available = new ArrayList<>();
        available.add(o4);
        assertEquals(o4, negotiation.getBestMatch(available), ERROR_MSG);
        available.add(o3);
        assertEquals(o3, negotiation.getBestMatch(available), ERROR_MSG);
        available.add(o2);
        assertEquals(o2, negotiation.getBestMatch(available), ERROR_MSG);
        available.add(o1);
        assertEquals(o1, negotiation.getBestMatch(available), ERROR_MSG);
    }

    @Test
    public void mediaTypeQualityFactor() {

        String header1 = "text/*;q=0.3, text/html;q=0.7, text/html;level=1";
        String header2 = "text/html;level=2;q=0.4, */*;q=0.5";
        ServerDrivenNegotiation negotiation = new ServerDrivenNegotiation();
        negotiation.setAcceptHeaders(Arrays.asList(header1, header2));
        negotiation.setAcceptLanguageHeaders(Arrays.asList("en"));

        Variant q03 = new Variant(MediaType.valueOf("text/plain"), (String) null, null);
        Variant q04 = new Variant(MediaType.valueOf("text/html;level=2"), (String) null, null);
        Variant q05 = new Variant(MediaType.valueOf("image/jpeg"), (String) null, null);
        Variant q07 = new Variant(MediaType.valueOf("text/html"), (String) null, null);
        Variant q07plus = new Variant(MediaType.valueOf("text/html;level=3"), (String) null, null);
        Variant q10 = new Variant(MediaType.valueOf("text/html;level=1"), (String) null, null);

        List<Variant> available = new ArrayList<Variant>();
        available.add(q03);
        assertEquals(q03, negotiation.getBestMatch(available), ERROR_MSG);
        available.add(q04);
        assertEquals(q04, negotiation.getBestMatch(available), ERROR_MSG);
        available.add(q05);
        assertEquals(q05, negotiation.getBestMatch(available), ERROR_MSG);
        available.add(q07);
        assertEquals(q07, negotiation.getBestMatch(available), ERROR_MSG);
        available.add(q07plus);
        assertEquals(q07plus, negotiation.getBestMatch(available), ERROR_MSG);
        available.add(q10);
        assertEquals(q10, negotiation.getBestMatch(available), ERROR_MSG);
    }

}
